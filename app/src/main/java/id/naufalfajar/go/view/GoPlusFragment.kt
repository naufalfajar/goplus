package id.naufalfajar.go.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentGoPlusBinding

class GoPlusFragment : Fragment() {
    private var _binding: FragmentGoPlusBinding? = null
    private val binding get() = _binding!!
    private var opened: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGoPlusBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(true)
        addStop()
        onBack()
        moveToNavigate()
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            changeStatusBarColor(false)
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

    private fun addStop(){
        binding.apply {
            tvTambahPemberhentian.setOnClickListener {
                if(!opened){
                    dotLine2.visibility = View.VISIBLE
                    dotLocation2.visibility = View.VISIBLE
                    etPemberhentian2.visibility = View.VISIBLE
                    dotLine2.alpha = 0f
                    dotLocation2.alpha = 0f
                    etPemberhentian2.alpha = 0f

                    dotLine2.animate()
                        .translationY(0f)
                        .alpha(1.0f)
                        .setDuration(500)
                        .setListener(null)

                    dotLocation2.animate()
                        .translationY(0f)
                        .alpha(1.0f)
                        .setDuration(750)
                        .setListener(null)

                    etPemberhentian2.animate()
                        .translationY(0f)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(null)
                }
                else{
                    dotLine2.animate()
                        .translationY(-dotLine2.height.toFloat())
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(null)

                    dotLocation2.animate()
                        .translationY(-dotLocation2.height.toFloat())
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(null)

                    etPemberhentian2.animate()
                        .translationY(-etPemberhentian2.height.toFloat())
                        .alpha(0.0f)
                        .setDuration(750)
                        .setListener(null)
                }
                opened = !opened
            }
        }
    }

    private fun moveToNavigate(){
        binding.mbtnGo.setOnClickListener {
            findNavController().navigate(GoPlusFragmentDirections.actionGoPlusFragmentToNavigationFragment())
        }
    }
}