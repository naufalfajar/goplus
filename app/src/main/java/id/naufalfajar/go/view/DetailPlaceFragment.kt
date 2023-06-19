package id.naufalfajar.go.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.adapter.PlaceAdapter
import id.naufalfajar.go.databinding.FragmentDetailPlaceBinding
import id.naufalfajar.go.model.Place

class DetailPlaceFragment : Fragment() {
    private var _binding: FragmentDetailPlaceBinding? = null
    private val binding get() = _binding!!
    private var placeId: Int? = 0
    private var lat: Double? = 0.0
    private var lng: Double? = 0.0
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailPlaceBinding.inflate(layoutInflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getItemId()
        getData()
        onBack()
//        goPlus()
    }

//    private fun goPlus(){
//        binding.btnGoplus.setOnClickListener {
//            findNavController().navigate(DetailPlaceFragmentDirections.actionDetailPlaceFragmentToNavigationFragment(
//                lat!!.toFloat(), lng!!.toFloat()))
//        }
//    }

    private fun getItemId(){
        placeId = DetailPlaceFragmentArgs.fromBundle(arguments as Bundle).id
    }

    private fun getData(){
        db = FirebaseFirestore.getInstance()
        db.collection("place")
            .whereEqualTo("id", placeId)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val dataPlace = data.toObject(Place::class.java)
                        if(dataPlace != null){
                            lat = dataPlace.location?.latitude
                            lng = dataPlace.location?.longitude
                            binding.apply {
                                Glide.with(requireActivity())
                                    .load(dataPlace.image)
                                    .into(ivPlace)
                                tvDescriptionPlace.text = dataPlace.description
                                tvNamePlace.text = dataPlace.name
                            }
                        }
                    }
                }

            }
            .addOnCompleteListener {
                binding.pbDetailPlace.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}