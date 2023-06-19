package id.naufalfajar.go.view.onboarding.screen

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentHomeBinding
import id.naufalfajar.go.databinding.FragmentThirdScreenBinding
import id.naufalfajar.go.helper.DataStoreManager
import id.naufalfajar.go.view.LoginFragmentDirections
import kotlinx.coroutines.launch

class ThirdScreen : Fragment() {
    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!
//    private lateinit var preferenceManager: DataStoreManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentThirdScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        preferenceManager = DataStoreManager(requireContext())
//        lifecycleScope.launch {
//            preferenceManager.isFirstTimeUser.collect { isFirstTime ->
//                if (!isFirstTime) {
//                    // Lakukan sesuatu untuk pengguna baru
//                    // Navigasikan ke halaman onboarding
//                    findNavController().navigate(ThirdScreenDirections.actionThirdScreenToHomeFragment())
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun moveToFourthScreen(){
        binding.mbtnContinue.setOnClickListener {
            findNavController().navigate(ThirdScreenDirections.actionThirdScreenToFourthScreen())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveToFourthScreen()
    }
}