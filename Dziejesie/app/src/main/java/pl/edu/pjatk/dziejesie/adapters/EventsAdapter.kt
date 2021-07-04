package pl.edu.pjatk.dziejesie.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import pl.edu.pjatk.dziejesie.*
import pl.edu.pjatk.dziejesie.databinding.EventItemBinding
import pl.edu.pjatk.dziejesie.models.EventDTO
import java.text.SimpleDateFormat

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

class EventsItemHolder(private val binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(event: EventDTO){
        binding.eventName.text = event.name.toString()
        var timestamp = event.date
        var date = timestamp.toDate()
        binding.eventDate.text = sdf.format(date)
        binding.eventPlace.text = event.place.toString()
        Picasso.get().load(event.photo).into(binding.eventImage)
    }

}

class EventsAdapter(private val eventsFragment: EventsFragment) : RecyclerView.Adapter<EventsItemHolder>(){
    private var events = emptyList<EventDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsItemHolder {
        val binding = EventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return EventsItemHolder(binding).apply {
            binding.root.setOnClickListener{
                false
            }
        }
    }

    override fun onBindViewHolder(holder: EventsItemHolder, position: Int) {
        holder.onBind(events[position])
        holder.itemView.setOnClickListener{
            eventsFragment.viewDetails(events[position])
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateData(data: List<EventDTO>){
        val eventsDiff = EventsDiff(events, data)
        events = data
        DiffUtil.calculateDiff(eventsDiff).dispatchUpdatesTo(this)
    }

    fun getLatLang(): List<LatLng> {
        return events.mapNotNull { item ->
            LatLng(item.lat,item.lng)
        }
    }

    class EventsDiff(val oldList: List<EventDTO>, val newList: List<EventDTO>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] === newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

    }
}