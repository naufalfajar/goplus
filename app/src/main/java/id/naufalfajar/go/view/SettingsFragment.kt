package id.naufalfajar.go.view

import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeProfile()
        changePassword()
        goBack()
        signOut()
    }

    private fun changeProfile(){
        binding.mbtnChangeProfile.setOnClickListener {
            showCustomDialog()
        }
    }

    private fun changePassword(){
        binding.mbtnChangePassword.setOnClickListener {
            showCustomDialogPass()
        }
    }

    private fun showCustomDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_confirm_change)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnYes: MaterialButton = dialog.findViewById(R.id.mbtn_yes)
        val btnNo: MaterialButton = dialog.findViewById(R.id.mbtn_no)

        btnYes.setOnClickListener {
            updateProfile()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCustomDialogPass(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_confirm_change)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnYes: MaterialButton = dialog.findViewById(R.id.mbtn_yes)
        val btnNo: MaterialButton = dialog.findViewById(R.id.mbtn_no)

        btnYes.setOnClickListener {
            updatePassword()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateProfile(){
        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = binding.etNama.text.toString()
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Berhasil Update Nama", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "User profile updated.")
                }
            }
        val email = binding.etEmail.text.toString()
        user.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Berhasil Update Email", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "User email address updated.")
                }
            }
    }

    private fun updatePassword(){
        val user = Firebase.auth.currentUser
        val newPassword = binding.etPasswordBaru.text.toString()

        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Berhasil Update Password", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "User password updated.")
                }
            }
    }

    private fun signOut(){
        binding.mbtnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val action = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true) // ID dari home fragment atau initial destination
                .build()
            findNavController().navigate(action, navOptions)
        }
    }

    private fun goBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}