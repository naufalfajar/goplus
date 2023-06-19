package id.naufalfajar.go.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.R
import id.naufalfajar.go.adapter.GoPlaceAdapter
import id.naufalfajar.go.adapter.PlaceAdapter
import id.naufalfajar.go.databinding.FragmentGoPlusBinding
import id.naufalfajar.go.model.Place

class GoPlusFragment : Fragment() {
    private var _binding: FragmentGoPlusBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var placeList: ArrayList<Place>
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
        addStop()
        getData()
        onBack()
        moveToNavigate()
    }

    private fun getData(){
        placeList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("place").get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val dataPlace = data.toObject(Place::class.java)
                        if(dataPlace != null){
                            placeList.add(dataPlace)
                        }
                    }
                }
                binding.rvPlace.apply {
                    adapter = GoPlaceAdapter(placeList)
                    layoutManager = LinearLayoutManager(requireContext())
                }


            }
            .addOnCompleteListener {
                binding.pbRvPlace.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
            }
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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
//            findNavController().navigate(GoPlusFragmentDirections.actionGoPlusFragmentToNavigationFragment())
        }
    }
}