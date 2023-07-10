package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.User
import it.unibs.mp.horace.backend.UserNotificationManager
import it.unibs.mp.horace.databinding.BottomSheetUserDetailsBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDetailsBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetUserDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore

        val uid = requireArguments().getString("uid")!!
        val user = CurrentUser()

        // If the user is viewing their own profile, hide the request friendship button.
        if (user.uid == uid) {
            binding.requestFriendship.visibility = View.INVISIBLE
        }

        db.collection(User.COLLECTION_NAME).document(uid).get().addOnSuccessListener {
            binding.photo.load(it.getString(User.PHOTO_FIELD))
            binding.username.text = it.getString(User.USERNAME_FIELD)
            binding.email.text = it.getString(User.EMAIL_FIELD)
        }

        binding.requestFriendship.setOnClickListener {
            lifecycleScope.launch {
                UserNotificationManager().sendFriendRequest(
                    db.collection(User.COLLECTION_NAME).document().get().await()
                        .toObject(User::class.java)!!,
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}