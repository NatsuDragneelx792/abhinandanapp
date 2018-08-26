package com.example.abhinandan.test1.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.Songs
import com.example.abhinandan.test1.fragments.MainScreenFragment
import com.example.abhinandan.test1.fragments.SongPlayingFragment

class MainScreenAdapter(_songDetails : ArrayList<Songs>, _contex : Context) :
        RecyclerView.Adapter<MainScreenAdapter.MainViewHolder>(){

    var songdetails : ArrayList<Songs>? = null
    var con : Context? = null

    init{
        this.songdetails = _songDetails
        this.con = _contex
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainViewHolder {
        var itemview = LayoutInflater.from(p0?.context)
                .inflate(R.layout.row_custom_mainscreen,p0,false)
        val returnthis = MainViewHolder(itemview)
        return returnthis
    }

    override fun getItemCount(): Int {
        if(songdetails == null){
            return 0
        }else {
            return (songdetails as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(p0: MainViewHolder, p1: Int) {
        var songobject = songdetails?.get(p1)
        p0.tracktitle?.text = songobject?.songtiitle
        p0.trackartist?.text = songobject?.artist
        p0.contentHolder?.setOnClickListener({
            if(SongPlayingFragment.statified.mediaplayer?.isPlaying != null){
                SongPlayingFragment.statified.mediaplayer?.pause()
                SongPlayingFragment.statified.currentson?.isplaying = false
            }
            val songplayingfragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songartist",songobject?.artist)
            args.putString("songtitle",songobject?.songtiitle)
            args.putString("path",songobject?.songdata)
            args.putInt("songid",songobject?.Songid?.toInt() as Int)
            args.putInt("songpos",p1)
            args.putParcelableArrayList("songdata",songdetails)
            songplayingfragment?.arguments = args
            (con as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment,songplayingfragment).addToBackStack("songplayingfragmentmain").commit()
        })

    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tracktitle : TextView? = null
        var trackartist : TextView? = null
        var contentHolder : RelativeLayout? = null

        init{
            tracktitle = itemView?.findViewById(R.id.tracktitle)
            trackartist = itemView?.findViewById(R.id.trackartist)
            contentHolder = itemView?.findViewById(R.id.contentrow)
        }


    }

}