package id.naufalfajar.go.model

import com.google.firebase.firestore.GeoPoint

data class Place(
    val name: String? = null,
    val image: String? = null,
    val description: String? = null,
    val id: Int? = null,
    val location: GeoPoint?= null
)
