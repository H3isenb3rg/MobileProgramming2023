package it.unibs.mp.horace.ui.auth

import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.BottomSheetAuthBinding


class AuthBottomSheet : BottomSheetDialogFragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: BottomSheetAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var oneTapClient: SignInClient
    private lateinit var googleSignInResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var apiErrorSnackbar: Snackbar
    private lateinit var noAccountErrorSnackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        apiErrorSnackbar = Snackbar.make(view, getString(R.string.api_error), Snackbar.LENGTH_SHORT)
        noAccountErrorSnackbar =
            Snackbar.make(view, getString(R.string.api_error), Snackbar.LENGTH_SHORT)

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
        // Adds a new listener for when the activity is launched after the One Tap UI is closed
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
                                AuthBottomSheetDirections.actionGlobalHomeFragment(
                                    resources.getString(R.string.source_sign_in)
                                )
                            } else {
                                // This should never happen
                                throw IllegalStateException()
                            }
                        }
                } catch (e: ApiException) {
                    if (e.statusCode == CommonStatusCodes.CANCELED) {
                        // User closed the Google One Tap dialog
                        return@registerForActivityResult
                    }

                    // For any other error, show a message to the user
                    apiErrorSnackbar.show()
                }
            }
    }

    private fun signInWithGoogle() {
        // The Google One Tap client that will be used for authentication
        oneTapClient = Identity.getSignInClient(requireActivity())

        // The request specifies that only Google accounts should be shown
        // as sign-in options. Email and password credentials stored in Google are
        // not supported as an authentication option.
        // The accounts are not be filtered to include only the authenticated ones:
        // setting this to true would only show Google accounts that are already authenticated
        // in the app.
        // Finally, if only one Google account is available, auto select it.
        val signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false).build()
        ).setAutoSelectEnabled(true).build()

        // Send the sign in request
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
            // If the request is successful, open the One Tap UI through the given intent.
            val request = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
            googleSignInResultLauncher.launch(request)
        }.addOnFailureListener {
            // The request is unsuccessful if the user has no available Google account.
            noAccountErrorSnackbar.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}