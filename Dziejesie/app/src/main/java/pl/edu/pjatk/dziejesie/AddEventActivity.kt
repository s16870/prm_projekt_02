package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import pl.edu.pjatk.dziejesie.databinding.AddEventActivityBinding
import pl.edu.pjatk.dziejesie.models.EventDTO
import pl.edu.pjatk.dziejesie.services.Geolocation
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


const val COORDS_REQUEST = 10

class AddEventActivity : AppCompatActivity()  {

    private lateinit var binding: AddEventActivityBinding
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var fileName: String
    private var viewMode = "NEW"
    private lateinit var latLng: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEventActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println(intent.extras?.get("id"))
        if(intent.extras?.get("id") != null){
            setupEditMode(intent.extras?.get("id").toString())
        }else{
            viewMode = "NEW"
            var bitmap = intent.extras?.get("image") as Bitmap
            binding.addEventImage.setImageBitmap(bitmap)

            Geolocation.getLocationLatLng {
                latLng = it
                var geocoder = Geocoder(applicationContext,Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val location: String = addresses[0].getAddressLine(0)
                binding.addEventPlace.setText(location.toString())
            }
            val date = Date()
            var currentDateString = sdf.format(date)
            binding.addEventDate.setText(currentDateString)
            setupButtons(bitmap)
        }
        binding.addEventPlace.setOnClickListener {
            val addEventIntent = Intent(applicationContext, MapSelector::class.java)
            startActivityForResult(addEventIntent, COORDS_REQUEST)
        }
        println(viewMode)
    }

    private fun setupEditMode(id: String){
        viewMode = "EDIT"
        ServiceLocator.repository.getById(id){event ->
            binding.addEventName.setText(event.name)
            var timestamp = event.date
            var date = timestamp.toDate()
            binding.addEventDate.setText(sdf.format(date))
            binding.addEventPlace.setText(event.place)
            Picasso.get().load(event.photo).into(binding.addEventImage)
            binding.addEventDescription.setText(event.description)
            binding.addCancelBtn.setOnClickListener{
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            latLng = LatLng(event.lat,event.lng)
            binding.addItemBtn.setOnClickListener {
                var isValid = validate()
                if(isValid) {
                    val eventName = binding.addEventName
                    val eventDescription = binding.addEventDescription
                    val eventPlace = binding.addEventPlace
                    val eventDTO = EventDTO(
                            eventName.text.toString(),
                            eventPlace.text.toString(),
                            event.date,
                            event.photo,
                            event.userName,
                            eventDescription.text.toString(),
                            latLng.latitude,
                            latLng.longitude
                    )
                    ServiceLocator.repository.updateEvent(id,eventDTO)
                    Toast.makeText(
                            this@AddEventActivity,
                            "Zaktualizowano!",
                            Toast.LENGTH_SHORT
                    ).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

    }

    private fun setupButtons(bitmap: Bitmap) {
        binding.addCancelBtn.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.addItemBtn.setOnClickListener {
            var isValid = validate()
            if(isValid){
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
                val data = baos.toByteArray()
                val fileName = "EVENT_" + Timestamp.now() + "_" + binding.addEventName.text.toString() + ".jpg"
                var uploadTask = ServiceLocator.eventImagesRepo.child(fileName).putBytes(data)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ServiceLocator.eventImagesRepo.child(fileName).downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val eventName = binding.addEventName
                        val eventDescription = binding.addEventDescription
                        val eventPlace = binding.addEventPlace
                        val eventDTO = EventDTO(eventName.text.toString(), eventPlace.text.toString(), Timestamp.now(), task.result.toString(), ServiceLocator.auth.currentUser!!.email.toString(),eventDescription.text.toString(),latLng.latitude,latLng.longitude)
                        ServiceLocator.repository.addEvent(eventDTO)
                        Toast.makeText(
                                this@AddEventActivity,
                                "Zapisano!",
                                Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        task.exception?.let {
                            throw it
                        }

                    }
                }
            }
        }
    }
    private fun validate(): Boolean {
        val eventName = binding.addEventName
        val eventDescription = binding.addEventDescription
        val eventPlace = binding.addEventPlace
        var isValid = true
        if(eventName.text.toString() == ""){
            eventName.setError("Pole wymagane")
            isValid = false
        }else{
            eventName.setError(null)
        }
        if(eventDescription.text.toString() == ""){
            eventDescription.setError("Pole wymagane")
            isValid = false
        }else if(eventDescription.text.toString().length > 500){
            eventDescription.setError("Opis nie może przekraczać 500 znaków")
            isValid = false
        }else{
            eventDescription.setError(null)
        }
        if(eventPlace.text.toString() == ""){
            eventPlace.setError("Pole wymagane")
            isValid = false
        }else{
            eventPlace.setError(null)
        }
        return isValid
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.MODE_APPEND && requestCode == COORDS_REQUEST){
            val lat = data?.extras?.getDouble("lat")
            val lng = data?.extras?.getDouble("lng")
            println(lat)
            println(lng)
            if(lat != null && lng != null){
                latLng = LatLng(lat, lng)
                var geocoder = Geocoder(applicationContext,Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val location: String = addresses[0].getAddressLine(0)
                binding.addEventPlace.setText(location)
            }
        }
    }
}