package pl.edu.pjatk.dziejesie.services

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import pl.edu.pjatk.dziejesie.MainActivity

class Geolocation {

    companion object {
        private lateinit var mFusedLocationClient: FusedLocationProviderClient
        @JvmStatic fun init(mainActivity: MainActivity){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        }
        @SuppressWarnings("MissingPermission")
        @JvmStatic fun getLocationLatLng(onResponse: (LatLng) -> Unit){
            mFusedLocationClient.lastLocation.addOnSuccessListener {
                onResponse(LatLng(it.latitude,it.longitude))
            }
        }
    }

}