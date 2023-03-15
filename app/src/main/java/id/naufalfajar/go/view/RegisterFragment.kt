package id.naufalfajar.go.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveToSignIn()
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

    private fun inputValidated(etEmail: String, etPassword: String, etConfirmPass: String, isEqual: Boolean) : Boolean{
        return (etEmail.isEmpty()||etPassword.isEmpty()||etConfirmPass.isEmpty()||!isEqual)
    }
}