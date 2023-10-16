package id.naufalfajar.go.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.naufalfajar.go.R
import id.naufalfajar.go.adapter.PlaceAdapter
import id.naufalfajar.go.databinding.FragmentHomeBinding
import id.naufalfajar.go.helper.DataStoreManager
import id.naufalfajar.go.view.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment()
{
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: DataStoreManager
    private val viewModel: HomeViewModel by viewModels()

    // Membuat instance ActivityResultLauncher untuk menangani hasil permintaan izin
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.fetchLocation()
            } else {
                Toast.makeText(requireContext(), "Akses Lokasi Ditolak", Toast.LENGTH_SHORT).show()
            }
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
        preferenceManager = DataStoreManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstTimetoFalse()

        checkPermission()
        setupData()
        searchFilterQuery()

        observePermissionDenied()
        observeLocation()
        observeLoadingState()
        observeUsername()
        observePlaces()
        observeErrorMessages()

        moveToGoPlus()
        moveToHistory()
        moveToSchedule()
        moveToSettings()
    }

    override fun onResume() {
        super.onResume()
        binding.etSearch.text.clear()
        checkGPSAndRequestToEnable()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setFirstTimetoFalse(){
        preferenceManager = DataStoreManager(requireContext())
        lifecycleScope.launch {
            preferenceManager.setFirstTimeUser(false)
        }
    }

    private fun checkPermission(){
        // Cek permission
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Jika permission sudah diberikan, kita akan melanjutkan untuk mendapatkan lokasi
                viewModel.fetchLocation()
            }
            else -> {
                // Jika permission belum diberikan, kita akan meminta permission kepada pengguna
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun checkGPSAndRequestToEnable() {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(requireContext())
                .setMessage("GPS belum aktif. Aktifkan GPS untuk melanjutkan.")
                .setPositiveButton("Aktifkan GPS") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun setupData(){
        // Ambil username saat view dibuat
        viewModel.getUsername()
        // Mengambil data places dari Firestore
        viewModel.fetchPlaces()
    }

    private fun searchFilterQuery(){
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Implement jika diperlukan
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Update query pencarian setiap kali teks berubah
                viewModel.updateSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Implement jika diperlukan
            }
        })
    }

    private fun observePermissionDenied(){
        // Observasi pesan kesalahan
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun observeLocation() {
        viewModel.location.observe(viewLifecycleOwner) { location ->
            binding.tvFlexLocation.text = location
        }
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbRvPlace.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeUsername() {
        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.namaemail.text = resources.getString(R.string.home_greetings, username)
        }
    }

    private fun observePlaces() {
        viewModel.filteredPlaces.observe(viewLifecycleOwner) { filteredPlaces ->
            val adapter = PlaceAdapter(ArrayList(filteredPlaces))
            binding.rvPlace.adapter = adapter
            binding.rvPlace.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeErrorMessages(){
        // Observasi pesan kesalahan
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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