package pl.edu.pjatk.dziejesie

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import pl.edu.pjatk.dziejesie.models.EventDTO

@SuppressWarnings("MissingPermission")
object Geofencing {

    private var wasCreated = false
    private lateinit var pendingIntentOld: PendingIntent

    fun createGeofence(context: Context, events: List<EventDTO>) {
        var geofenceList: MutableList<Geofence> = mutableListOf()
        events.forEach {
            val geof = Geofence.Builder()
                    .setRequestId(it.id)
                    .setCircularRegion(it.lat, it.lng, RANGE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build()
            geofenceList.add(geof)
        }

        var geofenceRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
        /*if(wasCreated){
            LocationServices.getGeofencingClient(context.applicationContext)
                    .removeGeofences(pendingIntentOld)
        }*/
        val pendingIntent = generatePendingIntent(context)
        pendingIntentOld = pendingIntent
        LocationServices.getGeofencingClient(context)
                .addGeofences(geofenceRequest, pendingIntent).run{
                    addOnSuccessListener { println("geofence added") }
                    addOnFailureListener { println("exception $it") }
                }
        wasCreated = true
/*
        if (context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", "pl.edu.pjatk.dziejesie", null)
            }.let {
                context.startActivity(it)
            }
        } else {
            if(wasCreated){
                LocationServices.getGeofencingClient(context.applicationContext)
                        .removeGeofences(pendingIntentOld)
            }
            val pendingIntent = generatePendingIntent(context)
            pendingIntentOld = pendingIntent
            LocationServices.getGeofencingClient(context)
                    .addGeofences(geofenceRequest, pendingIntent).run{
                        addOnSuccessListener { println("geofence added") }
                        addOnFailureListener { println("exception $it") }
                    }
            wasCreated = true
        }*/
    }

    private fun generatePendingIntent(context: Context): PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getBroadcast(
                        context.applicationContext, 0,
                        Intent(context, GeofenceBroadcastReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                        context.applicationContext, 0,
                        Intent(context, GeofenceBroadcastReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
}