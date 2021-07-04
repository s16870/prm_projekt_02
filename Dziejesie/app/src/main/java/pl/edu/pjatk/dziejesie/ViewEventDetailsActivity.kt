package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import pl.edu.pjatk.dziejesie.databinding.ViewEventDetailsActivityBinding
import java.text.SimpleDateFormat

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

class ViewEventDetailsActivity : AppCompatActivity()   {
    private lateinit var binding: ViewEventDetailsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewEventDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.extras?.get("id").toString()
        ServiceLocator.repository.getById(id){event ->
            if(event.userName != ServiceLocator.auth.currentUser?.email.toString()){
                binding.editItemBtn.visibility = View.INVISIBLE
            }
            binding.editEventName.setText(event.name)
            var timestamp = event.date
            var date = timestamp.toDate()
            binding.editEventDate.text = sdf.format(date)
            binding.editEventPlace.setText(event.place)
            Picasso.get().load(event.photo).into(binding.editEventImage)
            binding.editEventDescription.setText(event.description)
            binding.editEventAuthor.setText(event.userName)
        }
        binding.editCancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.editItemBtn.setOnClickListener {
            var resultIntent = Intent()
            resultIntent.putExtra("id",id)
            setResult(Activity.MODE_APPEND, resultIntent )
            finish()
        }

    }

}