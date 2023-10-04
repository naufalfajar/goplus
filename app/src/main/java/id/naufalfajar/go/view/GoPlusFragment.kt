package id.naufalfajar.go.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.adapter.GoPlaceAdapter
import id.naufalfajar.go.databinding.FragmentGoPlusBinding
import id.naufalfajar.go.model.Place
import id.naufalfajar.go.view.detection.TextToSpeechHelper
import id.naufalfajar.go.view.navigate.NavigationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoPlusFragment : Fragment() {
    private var _binding: FragmentGoPlusBinding? = null
    private val binding get() = _binding!!
    private lateinit var placeList: ArrayList<Place>
    private lateinit var placeSearch: MutableMap<String, GeoPoint>
    private lateinit var db: FirebaseFirestore
    private lateinit var textToSpeechHelper: TextToSpeechHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoPlusBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        textToSpeechHelper.shutdown()
    }

    private fun onBack(){
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textToSpeechHelper = TextToSpeechHelper(requireContext())
        getData()
        onBack()
        moveToNavigate()
    }

    private fun getData() {
        placeList = arrayListOf()
        placeSearch = mutableMapOf()

        binding.pbRvPlace.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    db.collection("place").get().await()
                }

                for (data in querySnapshot.documents) {
                    val dataPlace = data.toObject(Place::class.java)
                    dataPlace?.let {
                        placeList.add(it)
                        placeSearch[it.name!!] = it.location!!
                    }
                }

                binding.rvPlace.apply {
                    adapter = GoPlaceAdapter(placeList)
                    layoutManager = LinearLayoutManager(requireContext())
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
            } finally {
                binding.pbRvPlace.visibility = View.GONE
            }
        }
    }

    private fun moveToNavigate(){
        binding.mbtnGo.setOnClickListener {
            val input = binding.etPemberhentian1.text
            var count = 0
            var lng = 0.0
            var lat = 0.0
            if(placeSearch.isNotEmpty()){
                for (entry in placeSearch) {
                    if (entry.key.lowercase().contains(input.toString().lowercase())) {
                        count += 1
                        lat = entry.value.latitude
                        lng = entry.value.longitude
                    }
                }
                if(count == 1){
                    val intent = Intent(requireContext(), NavigationActivity::class.java)
                    intent.putExtra("latitude", lat)
                    intent.putExtra("longitude", lng)
                    startActivity(intent)
                } else {
                    textToSpeechHelper.speak("Input anda kurang tepat")
                }
            }
        }
    }
}