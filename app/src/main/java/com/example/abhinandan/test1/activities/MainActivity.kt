package com.example.abhinandan.test1.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.adapters.NavigationDrawerAdapter
import com.example.abhinandan.test1.fragments.MainScreenFragment
import com.example.abhinandan.test1.fragments.SongPlayingFragment
import com.example.abhinandan.test1.utils.CaptureBroadcast

class MainActivity : AppCompatActivity(){

    var imagesfornav : IntArray = intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,
            R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    var navdrawericonslist : ArrayList<String> = arrayListOf()
    object staticfied {
        var drawer: DrawerLayout? = null
        var notifyman : NotificationManager? = null

    }
    var tracknotify : Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        MainActivity.staticfied.drawer = findViewById(R.id.drawer_layout)

        var toggle = ActionBarDrawerToggle(this@MainActivity,MainActivity.staticfied.drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.staticfied.drawer?.setDrawerListener(toggle)
        toggle.syncState()


        val mainscreenfragment = MainScreenFragment()

        this.supportFragmentManager.beginTransaction().add(R.id.details_fragment,mainscreenfragment,"MainScreenFragment").commit()

        var _navadapter = NavigationDrawerAdapter(navdrawericonslist,imagesfornav,this)
        _navadapter.notifyDataSetChanged()

        var navigation_recycler_view = findViewById<RecyclerView>(R.id.recycle)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navadapter
        navigation_recycler_view.setHasFixedSize(true)

        navdrawericonslist.add("All Songs")
        navdrawericonslist.add("Favourites")
        navdrawericonslist.add("Settings")
        navdrawericonslist.add("About Us")



        val intent = Intent(this@MainActivity,MainActivity::class.java)
        val pendingintent = PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis().toInt(),
                intent,0)
        tracknotify = Notification.Builder(this).setContentTitle("A Track is playing in the background")
                .setSmallIcon(R.drawable.echo_logo).setContentIntent(pendingintent).setOngoing(true).setAutoCancel(true).build()
        staticfied.notifyman = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    override fun onStart() {
        super.onStart()
        try{
            staticfied.notifyman?.cancel(1978)
        }catch(e : Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try{
            if(SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean){
                staticfied.notifyman?.notify(1978,tracknotify)
            }
        }catch(e :Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try{
            staticfied.notifyman?.cancel(1978)
        }catch(e : Exception){
            e.printStackTrace()
        }
    }


}
