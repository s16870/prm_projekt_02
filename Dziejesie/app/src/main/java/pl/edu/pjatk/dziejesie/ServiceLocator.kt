package pl.edu.pjatk.dziejesie

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import pl.edu.pjatk.dziejesie.models.EventDTO
import pl.edu.pjatk.dziejesie.services.EventRepository
import pl.edu.pjatk.dziejesie.services.Geolocation

object ServiceLocator {
    const val eventCollectionName: String = "Events"

    val auth by lazy {
        Firebase.auth
    }

    val mStorageRef by lazy {
        FirebaseStorage.getInstance().getReference()
    }
    private val imageRepository by lazy {
        mStorageRef.storage.reference
    }

    val eventImagesRepo by lazy {
        imageRepository.child("eventImages")
    }


    private val eventCollection by lazy {
        Firebase.firestore.collection(eventCollectionName)
    }

    val repository by lazy {
        EventRepository(eventCollection)
    }



}