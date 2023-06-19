package id.naufalfajar.go.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentRegisterBinding
import id.naufalfajar.go.helper.DataStoreManager
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var preferenceManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = DataStoreManager(requireContext())
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveToSignIn()
        oneTapUI()
        googleSignInRequest()
        registerAuth()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun moveToSignIn(){
        binding.signIn.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    private fun googleSignInRequest(){
        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun oneTapUI(){
        binding.btnGoogleSignIn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity()) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    e.localizedMessage?.let { Log.d(TAG, it) }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
//                    val username = credential.id
//                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = auth.currentUser
                                        reload()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                        }else -> {
                        // Shouldn't happen.
                        Log.d(TAG, "No ID token!")
                    }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }

    private fun registerAuth(){
        binding.apply {
            mbtnRegister.setOnClickListener {
                val etEmail = binding.etEmail.text.toString()
                val etPassword = binding.etPassword.text.toString()
                val etConfirmPass = binding.etPasswordConfirmation.text.toString()
                val isEqual = (etConfirmPass == etPassword)

                if(inputValidated(etEmail, etPassword, etConfirmPass, isEqual)){
                    Toast.makeText(requireContext(),"Please check and fill all the fields", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.createUserWithEmailAndPassword(etEmail, etPassword)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                Toast.makeText(requireContext(), "Register success.",
                                    Toast.LENGTH_SHORT).show()
                                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
    private fun reload(){
        lifecycleScope.launch {
            preferenceManager.isFirstTimeUser.collect { isFirstTime ->
                if (isFirstTime) {
                    // Lakukan sesuatu untuk pengguna baru
                    // Navigasikan ke halaman onboarding
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToThirdScreen())
                } else {
                    // Pengguna bukan pengguna baru
                    // Navigasikan ke halaman berikutnya setelah onboarding
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeFragment())
                }
            }
        }
    }
    private fun inputValidated(etEmail: String, etPassword: String, etConfirmPass: String, isEqual: Boolean) : Boolean{
        return (etEmail.isEmpty()||etPassword.isEmpty()||etConfirmPass.isEmpty()||!isEqual)
    }
}