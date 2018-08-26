package com.example.abhinandan.test1.fragments


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.abhinandan.test1.CurrentSongHolder
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.Songs
import com.example.abhinandan.test1.activities.MainActivity
import com.example.abhinandan.test1.databases.EchoDatabase
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.audiovis
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.currentpos
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.currentson
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.endtim
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.fab
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.favcontent
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.fetchsongs
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.glview
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.loopbut
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.mediaplayer
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.myactivity
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.nextbut
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.nextprev
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.playpause
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.previous
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.seekba
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.shufflebut
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.songartistview
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.songtitleview
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.starttime
import com.example.abhinandan.test1.fragments.SongPlayingFragment.statified.updatesongtime
import com.example.abhinandan.test1.fragments.SongPlayingFragment.ststg.onsongcomplete
import com.example.abhinandan.test1.fragments.SongPlayingFragment.ststg.playnext
import com.example.abhinandan.test1.fragments.SongPlayingFragment.ststg.playprevious
import com.example.abhinandan.test1.fragments.SongPlayingFragment.ststg.processinformation
import com.example.abhinandan.test1.fragments.SongPlayingFragment.ststg.updatetextviews
import com.example.abhinandan.test1.utils.CaptureBroadcast
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus



class SongPlayingFragment : android.support.v4.app.Fragment() {

    object statified{
        var msensormanager : SensorManager? = null
        var msensorlist : SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var myactivity : Activity? = null
        var mediaplayer : MediaPlayer? = null
        var starttime : TextView? = null
        var endtim : TextView? = null
        var playpause : ImageButton? = null
        var previous : ImageButton? = null
        var nextbut : ImageButton? = null
        var loopbut : ImageButton? = null
        var seekba : SeekBar? = null
        var songartistview : TextView? = null
        var songtitleview : TextView? = null
        var shufflebut : ImageButton? = null
        var currentson : CurrentSongHolder? = null
        var currentpos : Int = 0
        var fetchsongs : ArrayList<Songs>? = null
        var audiovis : AudioVisualization? = null
        var glview : GLAudioVisualizationView? = null
        var nextprev : ArrayList<Songs>? = null
        var updatesongtime = object : Runnable{
            override fun run() {
                val getcurrent = mediaplayer?.currentPosition
                starttime?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(getcurrent!!.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent!!.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent!!.toLong())))
                )
                seekba?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this,1000)
            }
        }
        var fab : ImageButton? = null
        var favcontent : EchoDatabase? = null

    }




    object ststg{
        var MY_SHUFFLE_PREF = "Shuffle Feature"
        var MY_LOOP_PREF = "Loop Feature"


        fun onsongcomplete(){
            if(currentson?.isshuffle as Boolean){
                playnext("PlayNextLikeNormalShuffle", currentson?.songTitle, currentson?.songArtist)
                currentson?.isplaying = true

            }else{
                if(currentson?.isloop as Boolean){
                    currentson?.isplaying = true

                    var nextsong = fetchsongs?.get(currentpos)
                    currentson?.songArtist = nextsong?.artist
                    currentson?.songTitle = nextsong?.songtiitle
                    currentson?.songPath = nextsong?.songdata
                    currentson?.songId = nextsong?.Songid
                    currentson?.currentpos = currentpos

                    updatetextviews(currentson?.songTitle as String,currentson?.songArtist as String)
                    mediaplayer?.reset()
                    try{
                        mediaplayer?.setDataSource(myactivity, Uri.parse(currentson?.songPath))
                        mediaplayer?.prepare()
                        mediaplayer?.start()
                        processinformation(mediaplayer as MediaPlayer)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }else{
                    playnext("PlayNextNormal", currentson?.songTitle, currentson?.songArtist)
                    currentson?.isplaying = true
                }
            }
            if(favcontent?.checkifidexists(currentson?.songTitle, currentson?.songArtist) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_off))
            }
        }

        fun updatetextviews(songtit : String, songart : String){
            var songtitupd = songtit
            var songartupd = songart
            if(songtit.equals("<unknown>",true)){
                songtitupd = "Unknown"

            }
            if(songart.equals("<unknown>",true)){
                songartupd = "Unknown"

            }
            songtitleview?.setText(songtitupd)
            songartistview?.setText(songartupd)
        }

        fun processinformation(mediaplayer : MediaPlayer){
            val finaltime = mediaplayer?.duration
            val starttimef = mediaplayer?.currentPosition
            seekba?.max = finaltime
            starttime?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(starttimef.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(starttimef.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(starttimef.toLong())))
            )
            endtim?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(finaltime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finaltime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finaltime.toLong())))
            )
            seekba?.setProgress(starttimef)
            Handler().postDelayed(updatesongtime,1000)
        }

        fun playnext(check : String,_songtitle: String?,_songartist: String?){
            nextprev = getsongsfromphone()
            var switchonp = myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
            var isallow = switchonp?.getString("action_sort_ascending","true")
            if(isallow.equals("true")){
                Collections.sort(nextprev, Songs.Statified.nameComparator)
            }else {
                Collections.sort(nextprev, Songs.Statified.dateComparator)
            }
            for(i in 0..nextprev?.size as Int - 1)
            {
                if((nextprev?.get(i)?.songtiitle.equals(_songtitle)) && (nextprev?.get(i)?.artist.equals(_songartist))){
                    currentpos = i
                }
            }
            var oldpos = currentpos
            if(check.equals("PlayNextNormal",true)){
                currentpos  = currentpos + 1
            }else if(check.equals("PlayNextLikeNormalShuffle",true)){
                var randomobj = Random()
                var randpos = randomobj.nextInt(fetchsongs?.size!!.plus(1) as Int)
                currentpos = randpos

            }
            if(currentpos == fetchsongs?.size){
                currentpos = 0
            }
            if(currentson?.isloop == true){
                currentpos = oldpos
            }
            var nextsong = fetchsongs?.get(currentpos)
            currentson?.songArtist = nextsong?.artist
            currentson?.songTitle = nextsong?.songtiitle
            currentson?.songPath = nextsong?.songdata
            currentson?.songId = nextsong?.Songid
            currentson?.currentpos = currentpos

            updatetextviews(currentson?.songTitle as String,currentson?.songArtist as String)
            mediaplayer?.reset()
            try{
                mediaplayer?.setDataSource(myactivity, Uri.parse(currentson?.songPath))
                mediaplayer?.prepare()
                mediaplayer?.start()
                processinformation(mediaplayer as MediaPlayer)
            }catch (e : Exception){
                e.printStackTrace()
            }
            if(favcontent?.checkifidexists(currentson?.songTitle, currentson?.songArtist) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_off))
            }
        }

        fun playprevious(_songtitle : String?,_songartist : String?){
            nextprev = getsongsfromphone()
            var switchonp = myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
            var isallow = switchonp?.getString("action_sort_ascending","true")
            if(isallow.equals("true")){
                Collections.sort(nextprev, Songs.Statified.nameComparator)
            }else{
                Collections.sort(nextprev, Songs.Statified.dateComparator)
            }
            for(i in 0..nextprev?.size as Int - 1)
            {
                if((nextprev?.get(i)?.songtiitle.equals(_songtitle)) && (nextprev?.get(i)?.artist.equals(_songartist))){
                    currentpos = i
                }
            }
            var oldpos = currentpos
            currentpos = currentpos - 1
            if(currentpos == -1){
                currentpos = 0
            }
            if(currentson?.isplaying as Boolean){
                playpause?.setBackgroundResource(R.drawable.pause_icon)
            }else{
                playpause?.setBackgroundResource(R.drawable.play_icon)
            }
            if(currentson?.isloop == true){
                currentpos = oldpos
                loopbut?.setBackgroundResource(R.drawable.loop_icon)
            }
            var nextsong = fetchsongs?.get(currentpos)
            currentson?.songArtist = nextsong?.artist
            currentson?.songTitle = nextsong?.songtiitle
            currentson?.songPath = nextsong?.songdata
            currentson?.songId = nextsong?.Songid
            currentson?.currentpos = currentpos

            updatetextviews(currentson?.songTitle as String,currentson?.songArtist as String)
            mediaplayer?.reset()
            try{
                mediaplayer?.setDataSource(myactivity, Uri.parse(currentson?.songPath))
                mediaplayer?.prepare()
                mediaplayer?.start()
                processinformation(mediaplayer as MediaPlayer)
            }catch (e : Exception){
                e.printStackTrace()
            }
            if(favcontent?.checkifidexists(currentson?.songTitle, currentson?.songArtist) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_off))
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



    }
    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = "Now Playing"
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)

        starttime = view?.findViewById(R.id.startime)
        endtim = view?.findViewById(R.id.endtime)
        playpause = view?.findViewById(R.id.playbutton)
        previous = view?.findViewById(R.id.previousbutton)
        nextbut = view?.findViewById(R.id.nextbutton)
        loopbut = view?.findViewById(R.id.loopbutton)
        seekba = view?.findViewById(R.id.actseekbar)
        songartistview = view?.findViewById(R.id.songartistmain)
        songtitleview = view?.findViewById(R.id.songtitlemain)
        shufflebut = view?.findViewById(R.id.shufflebutton)
        glview = view?.findViewById(R.id.visualizer_view)
        fab = view?.findViewById(R.id.favouriteicon)
        fab?.alpha = 0.8f
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audiovis = glview as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onResume() {
        super.onResume()
        audiovis?.onResume()
        statified.msensormanager?.registerListener(statified.msensorlist,
                statified.msensormanager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        audiovis?.onPause()
        statified.msensormanager?.unregisterListener(statified.msensorlist)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audiovis?.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2 = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                statified.myactivity?.onBackPressed()
                return false
            }
        }
        return false
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var _path : String? = null
        var _title : String? = null
        var _artist : String? = null
        var _songid : Int? = 0


        favcontent = EchoDatabase(myactivity)
        currentson = CurrentSongHolder()
        currentson?.isplaying = true
        currentson?.isloop = false
        currentson?.isshuffle = false


        try{
            _path = arguments?.getString("path")
            _title = arguments?.getString("songtitle")
            _artist = arguments?.getString("songartist")
            _songid = arguments?.getInt("songid" )
            currentpos = arguments?.getInt("songpos") as Int
            fetchsongs = arguments?.getParcelableArrayList("songdata")

            currentson?.songPath = _path
            currentson?.songTitle = _title
            currentson?.songArtist = _artist
            currentson?.songId = _songid as Long
            currentson?.currentpos = currentpos


        }catch(e : Exception){
            e.printStackTrace()
        }
        statified.songartistview?.setText(currentson?.songArtist)
        statified.songtitleview?.setText(currentson?.songTitle)

        var fromfav = arguments?.get("Favbottombar") as? String
        var frommain = arguments?.get("Mainbottombar") as? String
        if(fromfav!=null){
            statified.mediaplayer = FavouriteFragment.statified.mediaPlyer
        }else if(frommain!= null){
            statified.mediaplayer = MainScreenFragment.static.mediaplauer
        }else{
            mediaplayer = MediaPlayer()
            mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaplayer?.setDataSource(myactivity, Uri.parse(_path))
                mediaplayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaplayer?.start()
        }
        processinformation(mediaplayer as MediaPlayer)
        if(currentson?.isplaying as Boolean){
            playpause?.setBackgroundResource(R.drawable.pause_icon)
        }else{
            playpause?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer?.setOnCompletionListener {
                onsongcomplete()
        }
        clickhandler()

        var visualhandler = DbmHandler.Factory.newVisualizerHandler(myactivity as Context,0)
        audiovis?.linkTo(visualhandler)

        var prefsforshuffle = myactivity?.getSharedPreferences(ststg.MY_SHUFFLE_PREF,Context.MODE_PRIVATE)
        var isshufflenow = prefsforshuffle?.getBoolean("feature",false)
        if(isshufflenow as Boolean){
            currentson?.isshuffle = true
            currentson?.isloop = false
            shufflebut?.setBackgroundResource(R.drawable.shuffle_icon)
            loopbut?.setBackgroundResource(R.drawable.loop_white_icon)
        }else{
            currentson?.isshuffle = false
            shufflebut?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsforloop = myactivity?.getSharedPreferences(ststg.MY_LOOP_PREF,Context.MODE_PRIVATE)
        var isloopnow = prefsforloop?.getBoolean("feature",false)
        if(isloopnow as Boolean){
            currentson?.isshuffle = false
            currentson?.isloop = true
            shufflebut?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopbut?.setBackgroundResource(R.drawable.loop_icon)
        }else{
            currentson?.isloop = false
            loopbut?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if(favcontent?.checkifidexists(currentson?.songTitle, currentson?.songArtist) as Boolean){
            fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_on))
        }else{
            fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_off))
        }

    }

    fun clickhandler(){
        fab?.setOnClickListener({
            if(favcontent?.checkifidexists(currentson?.songTitle, currentson?.songArtist) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_off))
                favcontent?.deletefavourite(currentson?.songTitle, currentson?.songArtist)
                Toast.makeText(myactivity,"Removed from Favourites",Toast.LENGTH_SHORT).show()
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myactivity!!,R.drawable.favorite_on))
                favcontent?.storeasfavourite(currentson?.songId?.toInt(),currentson?.songArtist,currentson?.songTitle
                                            ,currentson?.songPath)
                Toast.makeText(myactivity,"Added to Favourites",Toast.LENGTH_SHORT).show()
            }
        })
        playpause?.setOnClickListener({

            if(mediaplayer?.isPlaying as Boolean){
                mediaplayer?.pause()
                currentson?.isplaying = false
                playpause?.setBackgroundResource(R.drawable.play_icon)
            }else{
                mediaplayer?.start()
                currentson?.isplaying = true
                playpause?.setBackgroundResource(R.drawable.pause_icon)
            }

        })
        loopbut?.setOnClickListener({
            var editorshuffle = myactivity?.getSharedPreferences(ststg.MY_SHUFFLE_PREF,Context.MODE_PRIVATE)?.edit()
            var editorloop = myactivity?.getSharedPreferences(ststg.MY_LOOP_PREF,Context.MODE_PRIVATE)?.edit()
            if(currentson?.isloop as Boolean){
                currentson?.isloop = false
                loopbut?.setBackgroundResource(R.drawable.loop_white_icon)
                editorloop?.putBoolean("feature",false)
                editorloop?.apply()
            }else{
                currentson?.isloop = true
                currentson?.isshuffle = false
                loopbut?.setBackgroundResource(R.drawable.loop_icon)
                shufflebut?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorloop?.putBoolean("feature",true)
                editorloop?.apply()
                editorshuffle?.putBoolean("feature",false)
                editorshuffle?.apply()
            }

        })
        shufflebut?.setOnClickListener({
            var editorshuffle = myactivity?.getSharedPreferences(ststg.MY_SHUFFLE_PREF,Context.MODE_PRIVATE)?.edit()
            var editorloop = myactivity?.getSharedPreferences(ststg.MY_LOOP_PREF,Context.MODE_PRIVATE)?.edit()
            if(currentson?.isshuffle as Boolean){
                currentson?.isshuffle = false
                shufflebut?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorshuffle?.putBoolean("feature",false)
                editorshuffle?.apply()
            }else{
                currentson?.isshuffle = true
                currentson?.isloop = false
                shufflebut?.setBackgroundResource(R.drawable.shuffle_icon)
                loopbut?.setBackgroundResource(R.drawable.loop_white_icon)
                editorshuffle?.putBoolean("feature",true)
                editorshuffle?.apply()
                editorloop?.putBoolean("feature",false)
                editorloop?.apply()
            }
        })
        nextbut?.setOnClickListener({
            playpause?.setBackgroundResource(R.drawable.pause_icon)
            currentson?.isplaying = true
            if(currentson?.isshuffle as Boolean){
                playnext("PlayNextLikeNormalShuffle", currentson?.songTitle, currentson?.songArtist)
            }else{
                playnext("PlayNextNormal", currentson?.songTitle, currentson?.songArtist)
            }
        })
        previous?.setOnClickListener({
            currentson?.isplaying = true
            if ( currentson?.isloop as Boolean){
                loopbut?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playprevious(currentson?.songTitle, currentson?.songArtist)
        })
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statified.msensormanager = statified.myactivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindshakelistener()
        setHasOptionsMenu(true)

    }


    fun bindshakelistener() {
        statified.msensorlist = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent?) {
                val x = p0!!.values[0]
                val y = p0!!.values[1]
                val z = p0!!.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 9) {
                    val prefs = statified.myactivity?.getSharedPreferences(statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        if(currentson?.isplaying == false){
                            playpause?.setBackgroundResource(R.drawable.pause_icon)
                        }
                        ststg.playnext("PlayNextNormal", currentson?.songTitle, currentson?.songArtist)
                    }
                }

            }
        }
    }








}
