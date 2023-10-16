package id.naufalfajar.go.view.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.naufalfajar.go.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> get() = _places

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(getApplication() as Context)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _filteredPlaces = MutableLiveData<List<Place>>()
    val filteredPlaces: LiveData<List<Place>> get() = _filteredPlaces

    fun fetchPlaces() {
        _isLoading.value = true  // Menampilkan ProgressBar
        val db = Firebase.firestore

        viewModelScope.launch {
            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    db.collection("place").get().await()
                }

                val placeList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Place::class.java)
                }

                _places.postValue(placeList)
                _filteredPlaces.postValue(placeList)  // <-- Atur _filteredPlaces dengan data yang sama
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)  // Menyembunyikan ProgressBar
            }
        }
    }

    fun fetchLocation() {
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            _errorMessage.value = "Akses Izin Lokasi Ditolak"
        }
    }

    private fun getLocation() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            try {
                val location = fusedLocationProviderClient.lastLocation.await()

                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        val address = addresses[0].getAddressLine(0)
                        _location.value = address
                    } catch (e: IOException) {
                        _errorMessage.value = "Tidak dapat mengambil data alamat lokasi"
                    }
                } else {
                    _errorMessage.value = "Tidak dapat mengambil data lokasi"
                }
            } catch (e: SecurityException) {
                // Handle the security exception
                _errorMessage.value = "Izin ditolak"
            } catch (e: Exception) {
                // Handle other exceptions
                _errorMessage.value = e.message
            }
        }
    }

    fun getUsername() {
        val user = Firebase.auth.currentUser
        if (user == null) {
            _errorMessage.value = "Pengguna tidak ditemukan"
        } else if (user.displayName.isNullOrBlank()) {
            val email = user.email
            val nama = email?.substringBefore("@") ?: "User"
            _username.value = nama
        } else {
            _username.value = user.displayName
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterPlaces(query)
    }

    private fun filterPlaces(query: String) {
        if (query.isNotEmpty()) {
            val filteredPlaces = _places.value?.filter {
                it.name?.contains(query, true) == true
            }
            _filteredPlaces.postValue(filteredPlaces ?: emptyList())
        } else {
            _filteredPlaces.postValue(_places.value ?: emptyList())
        }
    }
}
