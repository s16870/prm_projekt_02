package pl.edu.pjatk.dziejesie.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.edu.pjatk.dziejesie.MainActivity

class FragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentClasses: List<Class<out Fragment>>
) : FragmentStateAdapter(fragmentActivity) {
    private var fragments: MutableList<Fragment> = mutableListOf(Fragment(),Fragment())
    override fun getItemCount(): Int = fragmentClasses.size

    override fun createFragment(position: Int): Fragment {
        var fragment = fragmentClasses[position].newInstance()
        fragments[position] = fragment
        println("On Resume")
        return fragmentClasses[position].newInstance()
    }
    fun resumeActivity(position: Int){
        fragments[0].onPause()
        fragments[1].onPause()
        fragments[position].onResume()
    }
}