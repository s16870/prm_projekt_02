package pl.edu.pjatk.dziejesie

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import pl.edu.pjatk.dziejesie.databinding.MapFragmentBinding
import pl.edu.pjatk.dziejesie.models.EventDTO
import pl.edu.pjatk.dziejesie.services.Geolocation

const val RANGE = 1000f

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var  binding: MapFragmentBinding
    private lateinit var map: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var mainHandler : Handler
    private var wasCreated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MapFragmentBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        supportMapFragment = childFragmentManager.fragments[0] as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onResume() {
        super.onResume()
        if(wasCreated){
            updateEvents()
        }
    }
    override fun onPause() {
        super.onPause()
        if(wasCreated){
            mainHandler.removeCallbacks(updateMapTask)
        }
    }

    private val updateMapTask = object  : Runnable {
        override fun run(){
            updateEvents()
            mainHandler.postDelayed(this, 60000)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        wasCreated = true
        if(checkSelfPermission(binding.root.context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 1)
        }else{
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            Geolocation.getLocationLatLng {
                map.animateCamera(CameraUpdateFactory.newLatLng(it));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f));
            }
        }
        updateMapTask.run()
    }
    private fun updateEvents(){
        val events = EventsFragment.staticEventsAdapter
        map.clear()
        events.getLatLang().forEach { item ->
            markEvent(item)
        }
    }

    private fun markEvent(latLng: LatLng) {
        val circle = CircleOptions()
            .strokeWidth(10f)
            .center(latLng)
            .radius(RANGE.toDouble())
            .strokeColor(Color.RED)
            .fillColor(Color.parseColor("#22cc0000"))
        map.addCircle(circle)
    }

    @SuppressWarnings("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}