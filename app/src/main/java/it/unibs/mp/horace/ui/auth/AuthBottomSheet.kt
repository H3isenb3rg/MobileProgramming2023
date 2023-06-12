package it.unibs.mp.horace.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.BottomSheetAuthBinding


class AuthBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AuthBottomSheet"
    }

    private lateinit var auth: FirebaseAuth
    private var _binding: BottomSheetAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var oneTapClient: SignInClient
    private lateinit var googleSignInResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        setupGoogleSignIn()

        binding.btnEmail.setOnClickListener {
            val action = AuthBottomSheetDirections.actionAuthFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun setupGoogleSignIn() {
        googleSignInResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    // The id token should never be null
                    val idToken = credential.googleIdToken!!

                    // Get the firebase Google credential from the id token
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                AuthBottomSheetDirections.actionGlobalHomeFragment(
                                    resources.getString(
                                        R.string.source_sign_in
                                    )
                                )
                            } else {
                                // This should never happen
                                // TODO: Show an error to the user
                            }
                        }
                } catch (e: ApiException) {
                    Log.d(TAG, e.message ?: "ERROR")
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                        }

                        CommonStatusCodes.NETWORK_ERROR -> Log.d(
                            TAG,
                            "One-tap encountered a network error."
                        )

                        else -> Log.d(
                            TAG, "Couldn't get credential from result."
                                    + e.localizedMessage
                        )
                    }
                }
            }
    }

    private fun signInWithGoogle() {
        // The Google One Tap client that will be used for authentication
        oneTapClient = Identity.getSignInClient(requireActivity());

        // The request specifies that only Google accounts should be shown
        // as sign-in options. Email and password credentials stored in Google are
        // not supported as an authentication option.
        // The accounts are not be filtered to include only the authenticated ones:
        // setting this to true would only show Google accounts that are already authenticated
        // in the app.
        val signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false).build()
        ).build();

        // Send the sign in request
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                // If the request is successful, open the One Tap UI through the given intent.
                val req = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                googleSignInResultLauncher.launch(req)
            }.addOnFailureListener { error ->
                // The request is unsuccessful if the user has no available Google account.
                // An error message is shown to the user.
                // TODO: show the error to the user
                Log.d(TAG, error.localizedMessage ?: "ERROR")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}