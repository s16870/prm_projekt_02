package pl.edu.pjatk.dziejesie.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import pl.edu.pjatk.dziejesie.models.EventDTO
import java.text.SimpleDateFormat


class EventRepository(
    private val eventCollection: CollectionReference
) {


    fun getAll(onResponse: (List<EventDTO>) -> Unit){
        eventCollection.orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                documents.mapNotNull {
                    EventDTO(it.get("name").toString(),it.get("place").toString(), it.get("date") as Timestamp, it.get("photo").toString(), it.get("userName").toString(),it.get("description").toString(), it.get("lat") as Double, it.get("lng") as Double,it.id)
                }.let{onResponse(it)}
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    fun getById(id:String, onResponse: (EventDTO) -> Unit){
        eventCollection.document(id).get()
            .addOnSuccessListener { doc ->
                onResponse(EventDTO(doc.get("name").toString(),doc.get("place").toString(), doc.get("date") as Timestamp, doc.get("photo").toString(), doc.get("userName").toString(),doc.get("description").toString(), doc.get("lat") as Double, doc.get("lng") as Double,doc.id))
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun updateEvent(id: String, event: EventDTO){
        eventCollection.document(id).update("name",event.name,
                                            "place",event.place,
                                            "description",event.description,
                                            "lat",event.lat,
                                            "lng",event.lng)
    }

    fun addEvent(event: Any){
        eventCollection.add(event)
    }

    fun updateEvent(event: Any){
    }



}