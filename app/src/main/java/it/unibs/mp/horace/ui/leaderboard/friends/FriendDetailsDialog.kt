package it.unibs.mp.horace.ui.leaderboard.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.DialogFriendDetailsBinding
import it.unibs.mp.horace.ui.UserDetailsDialogDirections
import kotlinx.coroutines.launch

class FriendDetailsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogFriendDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<FriendDetailsDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogFriendDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val currentUser = CurrentUser()

        // Load the user's data.
        db.collection(User.COLLECTION_NAME).document(args.userId).get().addOnSuccessListener {
            // This should never fail
            val user = User.parse(it.data!!)

            // load() is provided by the Coil library.
            binding.imageviewPhoto.load(user.profilePhoto)
            binding.textviewUsername.text = user.username
            binding.textviewEmail.text = user.email

            binding.buttonRemoveFriend.setOnClickListener {
                lifecycleScope.launch {
                    currentUser.deleteFriend(user)
                    dismiss()
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