package id.naufalfajar.go.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.R
import id.naufalfajar.go.adapter.PlaceAdapter
import id.naufalfajar.go.databinding.FragmentHomeBinding
import id.naufalfajar.go.helper.DataStoreManager
import id.naufalfajar.go.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment()
{
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var placeList: ArrayList<Place>
    private lateinit var preferenceManager: DataStoreManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val REQUEST_CODE = 100
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        preferenceManager = DataStoreManager(requireContext())
        db = Firebase.firestore
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        getUsername()
        moveToGoPlus()
        moveToHistory()
        moveToSchedule()
        moveToSettings()
        setFirstTimetoFalse()
        getLastLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getData(){
        placeList = arrayListOf()
        binding.pbRvPlace.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    db.collection("place").get().await()
                }

                for (data in querySnapshot.documents) {
                    val dataPlace = data.toObject(Place::class.java)
                    dataPlace?.let { placeList.add(it) }
                }

                binding.rvPlace.apply {
                    adapter = PlaceAdapter(placeList)
                    layoutManager = LinearLayoutManager(requireContext())
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
            } finally {
                binding.pbRvPlace.visibility = View.GONE
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            lifecycleScope.launch {
                try {
                    val location = fusedLocationProviderClient.lastLocation.await()
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address>?
                        try {
                            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address = addresses?.getOrNull(0)?.getAddressLine(0) ?: "Lokasi tidak ditemukan"
                            binding.tvFlexLocation.text = address
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvFlexLocation.text = resources.getString(R.string.gagal_lokasi_saat_ini)
                }
            }
        } else {
            binding.tvFlexLocation.text = resources.getString(R.string.gagal_lokasi_saat_ini)
            askPermission()
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Izin diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setFirstTimetoFalse(){
        preferenceManager = DataStoreManager(requireContext())
        lifecycleScope.launch {
            preferenceManager.setFirstTimeUser(false)
        }
    }

    private fun getUsername(){
        val user = Firebase.auth.currentUser
        if(user==null)
            Toast.makeText(requireContext(), "getdata", Toast.LENGTH_SHORT).show()
        else if (user.displayName.isNullOrBlank()){
            val email = user.email
            var nama = ""
            val index = email?.indexOf("@")
            if (index != -1) {
                nama = email?.substring(0, index!!)!!
            }
            binding.namaemail.text = resources.getString(R.string.home_greetings, nama)
        } else {
            val nama = user.displayName
            binding.namaemail.text = resources.getString(R.string.home_greetings, nama)
        }
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

    private fun moveToSettings(){
        binding.ivMenuHamburger.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }
    }
}