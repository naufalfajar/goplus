package id.naufalfajar.go.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUsername()
        moveToGoPlus()
        moveToHistory()
        moveToSchedule()

        binding.mbtnLogout.setOnClickListener {
            Firebase.auth.signOut()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
    }

    private fun getUsername(){
        val user = Firebase.auth.currentUser
        if(user==null)
            Toast.makeText(requireContext(), "getdata", Toast.LENGTH_SHORT).show()
        else
            binding.namaemail.text = user.email
    }

    private fun moveToGoPlus(){
        binding.cvGoPlus.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGoPlusFragment())
        }
    }

    private fun moveToHistory(){
        binding.cvHistory.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHistoryFragment())
        }
    }

    private fun moveToSchedule(){
        binding.cvSchedule.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToScheduleFragment())
        }
    }
}