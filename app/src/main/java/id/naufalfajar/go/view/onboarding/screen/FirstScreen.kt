package id.naufalfajar.go.view.onboarding.screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import id.naufalfajar.go.MainActivity
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentFirstScreenBinding
import id.naufalfajar.go.helper.DataStoreManager
import id.naufalfajar.go.view.RegisterFragmentDirections
import kotlinx.coroutines.launch

class FirstScreen : Fragment() {
    private var _binding: FragmentFirstScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstScreenBinding.inflate(layoutInflater, container, false)

        val btnContinue = binding.mbtnContinue
        val viewPager = activity?.findViewById<ViewPager2>(R.id.vp2_onboarding)

        btnContinue.setOnClickListener {
            viewPager?.currentItem = 1
        }
        return binding.root
    }

}