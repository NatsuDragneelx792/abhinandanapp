package com.example.abhinandan.test1.fragments


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.Songs
import com.example.abhinandan.test1.adapters.MainScreenAdapter
import com.example.abhinandan.test1.utils.CaptureBroadcast
import java.util.*


class MainScreenFragment : android.support.v4.app.Fragment() {

    var getSongsList : ArrayList<Songs>? = null
    var nowplayingbottombar : RelativeLayout? = null
    var playpausebutton : ImageView? = null
    var songtitle : TextView? = null
    var visiblelay : RelativeLayout? = null
    var nosongs : RelativeLayout? = null
    var recycleview : RecyclerView? = null
    var myactivity : Activity? = null
    var _mainscreenadapter : MainScreenAdapter? = null
    object static {
        var mediaplauer: MediaPlayer? = null
    }
    var trackpos :  Int = 0



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = "All Songs"
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        nowplayingbottombar = view?.findViewById(R.id.hiddenbarmainscreen)
        playpausebutton = view?.findViewById(R.id.playpausebutton)
        songtitle = view?.findViewById(R.id.songtitlemainscreen)
        visiblelay = view?.findViewById(R.id.visiblelayout)
        nosongs = view?.findViewById(R.id.nosongs)
        recycleview = view?.findViewById(R.id.contentmain)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = (context as Activity)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getsongsfromphone()
        val prefs = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if (getSongsList == null) {
            visiblelay?.visibility = View.INVISIBLE
            nosongs?.visibility = View.VISIBLE
            bottombarsetup()
        }else {
            _mainscreenadapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myactivity as Context)
            val layoutman = LinearLayoutManager(activity)
            recycleview?.layoutManager = layoutman
            recycleview?.itemAnimator = DefaultItemAnimator()
            recycleview?.adapter = _mainscreenadapter
            bottombarsetup()
        }
        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainscreenadapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainscreenadapter?.notifyDataSetChanged()
            }
        }
    }

        fun getsongsfromphone(): ArrayList<Songs> {
            var arraylist = arrayListOf<Songs>()
            var contentres = myactivity?.contentResolver
            var songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            var songcursor = contentres?.query(songuri, null, null, null, null)
            if (songcursor != null && songcursor.moveToFirst()) {
                val songid = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val songtitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songartist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val songdata = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val songdate = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                while (songcursor.moveToNext()) {
                    var currentid = songcursor.getLong(songid)
                    var currenttitle = songcursor.getString(songtitle)
                    var currentartist = songcursor.getString(songartist)
                    var currentdata = songcursor.getString(songdata)
                    var currentdate = songcursor.getLong(songdate)
                    arraylist.add(Songs(currentid, currenttitle, currentartist, currentdata, currentdate))

                }

            }
            return arraylist
        }


    fun bottombarsetup(){
        try{
            bottombarclickhandler()
            songtitle?.setText(SongPlayingFragment.statified.currentson?.songTitle)
            SongPlayingFragment.statified.mediaplayer?.setOnCompletionListener {
                songtitle?.setText(SongPlayingFragment.statified.currentson?.songTitle)
                SongPlayingFragment.ststg.onsongcomplete()
            }
            if(SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean){
                nowplayingbottombar?.visibility = View.VISIBLE
            }else{
                nowplayingbottombar?.visibility = View.INVISIBLE
            }

        }catch(e : Exception){
            e.printStackTrace()
        }
    }

    fun bottombarclickhandler() {
        nowplayingbottombar?.setOnClickListener({
            static.mediaplauer = SongPlayingFragment.statified.mediaplayer
            var songplayingfragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songartist", SongPlayingFragment.statified.currentson?.songArtist)
            args.putString("songtitle", SongPlayingFragment.statified.currentson?.songTitle)
            args.putString("path", SongPlayingFragment.statified.currentson?.songPath)
            args.putInt("songid", SongPlayingFragment.statified.currentson?.songId?.toInt() as Int)
            args.putInt("songpos", SongPlayingFragment.statified.currentson?.currentpos?.toInt() as Int)
            args.putParcelableArrayList("songdata", SongPlayingFragment.statified.fetchsongs)
            args.putString("Mainbottombar", "Success")
            SongPlayingFragment.statified.songtitleview?.setText(SongPlayingFragment.statified.currentson?.songTitle)
            SongPlayingFragment.statified.songartistview?.setText(SongPlayingFragment.statified.currentson?.songArtist)
            SongPlayingFragment.statified.seekba?.setProgress(SongPlayingFragment.statified.currentson?.trackpos as Int)
            songplayingfragment?.arguments = args
            SongPlayingFragment.ststg.updatetextviews(SongPlayingFragment.statified.currentson?.songTitle as String,
                    SongPlayingFragment.statified.currentson?.songArtist as String)
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, songplayingfragment)
                    ?.commit()

        })
        playpausebutton?.setOnClickListener({
            if(SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean){
                SongPlayingFragment.statified.mediaplayer?.pause()
                trackpos = SongPlayingFragment.statified.mediaplayer?.currentPosition?.toInt() as Int
                playpausebutton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.statified.mediaplayer?.seekTo(trackpos)
                SongPlayingFragment.statified.mediaplayer?.start()
                playpausebutton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            _mainscreenadapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editortwo = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent", "true")
            editortwo?.putString("action_sort_ascending", "false")
            editortwo?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            _mainscreenadapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

}
