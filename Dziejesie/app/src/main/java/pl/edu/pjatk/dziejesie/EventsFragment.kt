package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import pl.edu.pjatk.dziejesie.adapters.EventsAdapter
import pl.edu.pjatk.dziejesie.databinding.EventsFragmentBinding
import pl.edu.pjatk.dziejesie.models.EventDTO
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_ADD_ITEM = 2
const val REQUEST_VIEW_ITEM = 3
const val REQUEST_EDIT_ITEM = 4

class EventsFragment : Fragment() {
    private lateinit var binding: EventsFragmentBinding
    private lateinit var eventsAdapter: EventsAdapter
    companion object {
        @JvmStatic lateinit var staticEventsAdapter: EventsAdapter
    }
    private lateinit var mainHandler : Handler
    private var wasCreated = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return EventsFragmentBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    fun viewDetails(event: EventDTO){
        val viewEventIntent = Intent(context, ViewEventDetailsActivity::class.java)
        viewEventIntent.putExtra("id",event.id)
        startActivityForResult(viewEventIntent, REQUEST_VIEW_ITEM)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventsAdapter = EventsAdapter(this)
        staticEventsAdapter = eventsAdapter
        setupAddBtn()
        binding.eventsList.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = eventsAdapter
        }
        mainHandler = Handler(Looper.getMainLooper())
        updateEventsTask.run()
        wasCreated = true
    }
    private val updateEventsTask = object  : Runnable {
        override fun run(){
            loadData()
            mainHandler.postDelayed(this, 60000)
        }
    }

    override fun onResume() {
        super.onResume()
        if(wasCreated){
            updateEventsTask.run()
        }
    }

    override fun onPause() {
        super.onPause()
        if(wasCreated){
            mainHandler.removeCallbacks(updateEventsTask)
        }
    }

    private fun loadData() {
        ServiceLocator.repository.getAll(){
            eventsAdapter.updateData(it)
        }
    }

    private fun setupAddBtn() {
        binding.addEventBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE && data != null){
            val addEventIntent = Intent(context, AddEventActivity::class.java)
            addEventIntent.putExtra("image",data.extras?.get("data") as Bitmap)
            startActivityForResult(addEventIntent, REQUEST_ADD_ITEM)
        }else if(resultCode == Activity.RESULT_OK && (requestCode == REQUEST_ADD_ITEM || requestCode == REQUEST_EDIT_ITEM)){
            loadData()
        }else if(resultCode == Activity.MODE_APPEND && requestCode == REQUEST_VIEW_ITEM){
            val addEventIntent = Intent(context, AddEventActivity::class.java)
            addEventIntent.putExtra("id",data?.extras?.get("id").toString())
            startActivityForResult(addEventIntent, REQUEST_ADD_ITEM)
        }
    }


}