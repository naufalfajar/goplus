package id.naufalfajar.go.view.onboarding.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentFourthScreenBinding
import id.naufalfajar.go.databinding.FragmentThirdScreenBinding
import id.naufalfajar.go.helper.DataStoreManager
import kotlinx.coroutines.launch

class FourthScreen : Fragment() {
    private var _binding: FragmentFourthScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun moveToHome(){
        binding.mbtnContinue.setOnClickListener {
            findNavController().navigate(FourthScreenDirections.actionFourthScreenToHomeFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveToHome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}