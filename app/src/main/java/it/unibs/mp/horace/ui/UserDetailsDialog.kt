package it.unibs.mp.horace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.UserNotificationManager
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.DialogUserDetailsBinding
import kotlinx.coroutines.launch

class UserDetailsDialog : BottomSheetDialogFragment() {
    companion object {
        const val UID_ARGUMENT = "uid"
    }

    private var _binding: DialogUserDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val uid = requireArguments().getString(UID_ARGUMENT)!!

        // If the user is viewing their own profile, hide the request friendship button.
        if (CurrentUser().uid == uid) {
            binding.requestFriendship.visibility = View.INVISIBLE
        }

        // Load the user's data.
        db.collection(User.COLLECTION_NAME).document(uid).get().addOnSuccessListener {
            // This should never fail
            val user = it.toObject(User::class.java)!!

            // load() is provided by the Coil library.
            binding.photo.load(user.profilePhoto)
            binding.username.text = user.username
            binding.email.text = user.email

            binding.requestFriendship.setOnClickListener {
                lifecycleScope.launch {
                    UserNotificationManager().sendFriendRequest(user)
                    findNavController().navigate(
                        UserDetailsDialogDirections.actionGlobalHome(R.string.source_friend_request)
                    )
                }
            }
        }.addOnFailureListener {
            // If the user does not exist, go back to the home screen.
            findNavController().navigate(UserDetailsDialogDirections.actionGlobalHome())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}