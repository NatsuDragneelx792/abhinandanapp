package com.example.abhinandan.test1.fragments


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.Songs
import com.example.abhinandan.test1.adapters.FavAdapter
import com.example.abhinandan.test1.databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_song_playing.*



class FavouriteFragment : android.support.v4.app.Fragment() {
    var myactivity: Activity? = null
    var nofaourites: TextView? = null
    var nowplayingbottombar: RelativeLayout? = null
    var playpausebut: ImageButton? = null
    var songtit: TextView? = null
    var recycler: RecyclerView? = null
    var favcontent: EchoDatabase? = null
    var refreshlist: ArrayList<Songs>? = null
    var getlistfromdb: ArrayList<Songs>? = null
    var trackpos: Int = 0
    object statified {
        var mediaPlyer: MediaPlayer? = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = "Favourites"
        var view = inflater.inflate(R.layout.fragment_favourite, container, false)

        nofaourites = view?.findViewById(R.id.nofavourites)
        nowplayingbottombar = view?.findViewById(R.id.hiddenbarfavscreen)
        songtit = view?.findViewById(R.id.songtitlefavscreen)
        playpausebut = view?.findViewById(R.id.playpausebuttonfav)
        recycler = view?.findViewById(R.id.favouriterecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favcontent = EchoDatabase(myactivity)
        displayfavouritesbysearching()
        bottombarsetup()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
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

    fun bottombarsetup() {
        try {
            bottombarclickhandler()
            songtit?.setText(SongPlayingFragment.statified.currentson?.songTitle)
            SongPlayingFragment.statified.mediaplayer?.setOnCompletionListener {
                songtit?.setText(SongPlayingFragment.statified.currentson?.songTitle)
                SongPlayingFragment.ststg.onsongcomplete()
            }
            if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                nowplayingbottombar?.visibility = View.VISIBLE
            } else {
                nowplayingbottombar?.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottombarclickhandler() {
        nowplayingbottombar?.setOnClickListener({
            statified.mediaPlyer = SongPlayingFragment.statified.mediaplayer
            val songplayingfragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songartist", SongPlayingFragment.statified.currentson?.songArtist)
            args.putString("songtitle", SongPlayingFragment.statified.currentson?.songTitle)
            args.putString("path", SongPlayingFragment.statified.currentson?.songPath)
            args.putInt("songid", SongPlayingFragment.statified.currentson?.songId?.toInt() as Int)
            args.putInt("songpos", SongPlayingFragment.statified.currentson?.currentpos?.toInt() as Int)
            args.putParcelableArrayList("songdata", SongPlayingFragment.statified.fetchsongs)
            args.putString("Favbottombar", "Success")
            songplayingfragment?.arguments = args
            SongPlayingFragment.ststg.updatetextviews(SongPlayingFragment.statified.currentson?.songTitle as String,
                    SongPlayingFragment.statified.currentson?.songArtist as String)
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, songplayingfragment)
                    ?.addToBackStack("SongPlayingFragment")?.commit()

        })

        playpausebut?.setOnClickListener({
            if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.statified.mediaplayer?.pause()
                trackpos = SongPlayingFragment.statified.mediaplayer?.currentPosition?.toInt() as Int
                playpausebut?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.statified.mediaplayer?.seekTo(trackpos)
                SongPlayingFragment.statified.mediaplayer?.start()
                playpausebut?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

    }

    fun displayfavouritesbysearching() {
        if (favcontent?.checksize() as Int > 0) {
            refreshlist = ArrayList<Songs>()
            getlistfromdb = favcontent?.querydblist()
            var fetchlistfromdevice = getsongsfromphone()
            if (fetchlistfromdevice != null) {
                for (i in 0..fetchlistfromdevice?.size - 1) {
                    for (j in 0..getlistfromdb?.size as Int - 1) {
                        if ((getlistfromdb?.get(j)?.songtiitle.equals(fetchlistfromdevice?.get(i)?.songtiitle)) &&
                                (getlistfromdb?.get(j)?.artist.equals(fetchlistfromdevice?.get(i)?.artist))) {
                            refreshlist?.add((getlistfromdb as ArrayList<Songs>)[j])
                        }
                    }
                }
            } else {
            }
            if (refreshlist == null) {
                recycler?.visibility = View.INVISIBLE
                nofaourites?.visibility = View.VISIBLE
            } else {
                var favscreenadapter = FavAdapter(refreshlist as ArrayList<Songs>, myactivity as Context)
                val layoutman = LinearLayoutManager(activity)
                recycler?.layoutManager = layoutman
                recycler?.adapter = favscreenadapter
                recycler?.itemAnimator = DefaultItemAnimator()
                recycler?.setHasFixedSize(true)
            }
        }
        else
        {
            recycler?.visibility = View.INVISIBLE
            nofaourites?.visibility = View.VISIBLE
        }

    }
}




