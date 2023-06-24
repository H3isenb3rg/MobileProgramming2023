package it.unibs.mp.horace.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
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

    private lateinit var callbackManager: CallbackManager

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleIntentSender: ActivityResultLauncher<IntentSenderRequest>
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
        setupFacebookSignIn()

        binding.btnEmail.setOnClickListener {
            val action = AuthBottomSheetDirections.actionAuthFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        binding.btnGoogle.setOnClickListener {
            // The Google One Tap client that will be used for authentication
            oneTapClient = Identity.getSignInClient(requireActivity())

            // The request specifies that only Google accounts should be shown
            // as sign-in options. Email and password credentials stored in Google are
            // not supported as an authentication option.
            // The accounts are not be filtered to include only the authenticated ones:
            // setting this to true would only show Google accounts that are already authenticated
            // in the app.
            // Finally, if only one Google account is available, auto select it.
            signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false).build()
            ).setAutoSelectEnabled(true).build()

            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
                // If the request is successful, open the One Tap UI through the given intent.
                val request = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                googleIntentSender.launch(request)
            }.addOnFailureListener {
                // The request is unsuccessful if the user has no available Google account.
                noAccountErrorSnackbar.show()
            }
        }

        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                requireActivity(), listOf("public_profile", "user_friends")
            )
        }
    }

    private fun setupGoogleSignIn() {
        // Adds a new listener for when the activity is launched after the One Tap UI is closed
        googleIntentSender =
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
                                    resources.getString(R.string.sign_in)
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

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                AuthBottomSheetDirections.actionGlobalHomeFragment(
                                    resources.getString(R.string.sign_in)
                                )
                            } else {
                                throw IllegalStateException()
                            }
                        }
                }

                override fun onCancel() {
                    return
                }

                override fun onError(error: FacebookException) {
                    apiErrorSnackbar.show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}