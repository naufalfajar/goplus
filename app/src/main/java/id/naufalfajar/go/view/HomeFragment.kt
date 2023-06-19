package id.naufalfajar.go.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import id.naufalfajar.go.adapter.PlaceAdapter
import id.naufalfajar.go.databinding.FragmentHomeBinding
import id.naufalfajar.go.helper.DataStoreManager
import id.naufalfajar.go.model.Place
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import kotlin.math.sign

class HomeFragment : Fragment()
//    , PermissionsListener
{
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var placeList: ArrayList<Place>
//    private val permissionsManager = PermissionsManager(this)
    private lateinit var preferenceManager: DataStoreManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_CODE = 100
    }
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
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        preferenceManager = DataStoreManager(requireContext())
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
                    adapter = PlaceAdapter(placeList)
                    layoutManager =LinearLayoutManager(requireContext())
                }


            }
            .addOnCompleteListener {
                binding.pbRvPlace.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        getUsername()
//        validatePermission()
        moveToGoPlus()
        moveToHistory()
        moveToSchedule()
        signOut()
        setFirstTimetoFalse()
        getLastLocation()
    }

    private fun getLastLocation(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address>?
                        try {
                            addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
//                            "${addresses?.get(0)?.latitude}"+",${addresses?.get(0)?.longitude}"
                            binding.tvFlexLocation.text =
                                "${addresses?.get(0)
                                ?.getAddressLine(0)}"

                        } catch (e: IOException) {
                            binding.tvFlexLocation.text = "CATCH"
                            e.printStackTrace()
                        }
                    }
                })
        } else {
            binding.tvFlexLocation.text = "GAGAL"
//            validatePermission()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Required Permission", Toast.LENGTH_SHORT).show()
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

//    private fun validatePermission(){
//        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
//            requestOptionalPermissions()
//        } else {
//            permissionsManager.requestLocationPermissions(requireActivity())
//        }
//    }

//    override fun onPermissionResult(granted: Boolean) {
//        if (granted) {
//            requestOptionalPermissions()
//        } else {
//            Toast.makeText(
//                requireContext(),
//                "You didn't grant the permissions required to use the app",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }

//    @Suppress("DEPRECATION")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//    private fun requestOptionalPermissions() {
//        val permissionsToRequest = mutableListOf<String>()
//        // starting from Android R leak canary writes to Download storage without the permission
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//            PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
//            PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
//        }
//        if (permissionsToRequest.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                permissionsToRequest.toTypedArray(),
//                10
//            )
//        }
//    }

//    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
//        Toast.makeText(
//            requireContext(),
//            "This app needs location and storage permissions in order to show its functionality.",
//            Toast.LENGTH_LONG
//        ).show()
//    }
    private fun getUsername(){
        val user = Firebase.auth.currentUser
        if(user==null)
            Toast.makeText(requireContext(), "getdata", Toast.LENGTH_SHORT).show()
        else{
            val email = user.email
            var nama = ""
            val index = email?.indexOf("@")
            if (index != -1) {
                nama = email?.substring(0, index!!)!!
            }
            binding.namaemail.text = "Halo $nama. Ready to GO?"
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

    private fun signOut(){
        binding.mbtnLogout.setOnClickListener {
//            getLastLocation()
            Firebase.auth.signOut()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
    }
}