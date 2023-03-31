package id.naufalfajar.go.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentForgotPasswordBinding
import id.naufalfajar.go.databinding.FragmentLoginBinding


class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyEmail()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun verifyEmail(){
        binding.apply {
            mbtnVerify.setOnClickListener {
                val emailAddress = etEmail.text.toString()
                if(emailAddress.isEmpty()){
                    Toast.makeText(requireContext(), "Please fill the email",
                        Toast.LENGTH_SHORT).show()
                } else{
                    auth.setLanguageCode("id")
                    Firebase.auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email verification sent.")
                            }else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "Verification failed.", task.exception)
                                Toast.makeText(requireContext(), "Verification failed.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}