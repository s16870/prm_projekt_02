package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.facebook.FacebookSdk
import com.google.common.util.concurrent.MoreExecutors
import pl.edu.pjatk.dziejesie.adapters.FragmentAdapter
import pl.edu.pjatk.dziejesie.databinding.ActivityMainBinding
import pl.edu.pjatk.dziejesie.services.EventRepository
import pl.edu.pjatk.dziejesie.services.Geolocation
import java.util.*


const val LOGIN_REQUEST = 5

class MainActivity : AppCompatActivity() {
    enum class Pages(val fragmentClass: Class<out Fragment>, @IdRes val itemId: Int){
        EVENTS(EventsFragment::class.java, R.id.events),
        MAP(MapFragment::class.java,R.id.map)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentAdapter: FragmentAdapter
    private val pageChangedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.navigationBar.selectedItemId =  Pages.values()[position].itemId
            fragmentAdapter.resumeActivity(position)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Geolocation.init(this)
        createNotificationChannel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(ServiceLocator.auth.currentUser == null){
            binding.loginName.visibility = View.INVISIBLE
            binding.signOutBtn.visibility = View.INVISIBLE
            val addEventIntent = Intent(applicationContext, LoginView::class.java)
            startActivityForResult(addEventIntent, LOGIN_REQUEST)
        }else{
            binding.loginName.setText(ServiceLocator.auth.currentUser!!.email.toString())
        }
        setupPager()
        setupBottomNavigation()
        setupSignOutBtn()
    }

    private fun setupSignOutBtn() {
        binding.signOutBtn.setOnClickListener {
            ServiceLocator.auth.signOut()
            binding.loginName.visibility = View.INVISIBLE
            binding.signOutBtn.visibility = View.INVISIBLE
            val addEventIntent = Intent(applicationContext, LoginView::class.java)
            startActivityForResult(addEventIntent, LOGIN_REQUEST)
        }
    }

    private fun setupBottomNavigation() {
        binding.navigationBar.setOnNavigationItemSelectedListener { menuItem ->
            val page = Pages.values().first { it.itemId == menuItem.itemId }
            val index = Pages.values().indexOf(page)
            binding.pager.currentItem = index
            true
        }
    }

    private fun setupPager() {
        fragmentAdapter = FragmentAdapter(this, Pages.values().map { it.fragmentClass} )
        binding.pager.apply {
            adapter = fragmentAdapter
            registerOnPageChangeCallback(pageChangedCallback)
            setUserInputEnabled(false)
        }
        binding.pager.adapter = fragmentAdapter
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST) {
            binding.loginName.visibility = View.VISIBLE
            binding.signOutBtn.visibility = View.VISIBLE
            binding.loginName.setText(ServiceLocator.auth.currentUser?.email)
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "dzieje_sie_channel"
            val descriptionText = "dzieje sie"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("dzieje_sie_111", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}