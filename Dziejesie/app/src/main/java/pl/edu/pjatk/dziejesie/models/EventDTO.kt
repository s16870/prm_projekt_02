package pl.edu.pjatk.dziejesie.models

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName
import java.net.URI

data class EventDTO (
    @SerializedName("name")
    var name: String,
    @SerializedName("place")
    var place: String,
    @SerializedName("date")
    var date: Timestamp,
    @SerializedName("photo")
    var photo: String,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
    var id: String? = null
)