package pl.edu.pjatk.dziejesie

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import pl.edu.pjatk.dziejesie.databinding.MapDialogBinding
import pl.edu.pjatk.dziejesie.services.Geolocation

class MapSelector : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding : MapDialogBinding
    private lateinit var supportMapFragment : SupportMapFragment
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportMapFragment = supportFragmentManager.findFragmentById(R.id.dialog_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.apply{
            setOnMapClickListener { onSelected(it) }
        }
        if(checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 1)
        }else{
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            Geolocation.getLocationLatLng {
                map.animateCamera(CameraUpdateFactory.newLatLng(it));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f));
            }
        }
    }

    private fun onSelected(latLng: LatLng) {
        var resultIntent = Intent()
        resultIntent.putExtra("lat",latLng.latitude)
        resultIntent.putExtra("lng",latLng.longitude)
        setResult(Activity.MODE_APPEND, resultIntent )
        finish()
    }

}