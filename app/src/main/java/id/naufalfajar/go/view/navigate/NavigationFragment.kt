package id.naufalfajar.go.view.navigate

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment() {
    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavigationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(false)
        onBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            changeStatusBarColor(true)
            findNavController().popBackStack()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun changeStatusBarColor(code: Boolean){
        if (Build.VERSION.SDK_INT >= 21) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if(code)
                window.statusBarColor = requireActivity().getColor(R.color.its_white)
            else
                window.statusBarColor = requireActivity().getColor(R.color.its_blue)
        }
    }
}