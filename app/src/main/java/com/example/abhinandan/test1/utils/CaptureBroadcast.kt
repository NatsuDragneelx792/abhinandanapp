package com.example.abhinandan.test1.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.activities.MainActivity
import com.example.abhinandan.test1.fragments.SongPlayingFragment


class CaptureBroadcast : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try{
                MainActivity.staticfied.notifyman?.cancel(1978)
            }catch(e : Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                    Toast.makeText(SongPlayingFragment.statified.myactivity,"pause",Toast.LENGTH_SHORT).show()
                    SongPlayingFragment.statified.mediaplayer?.pause()
                    SongPlayingFragment.statified.playpause?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager = p0?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try{
                        MainActivity.staticfied.notifyman?.cancel(1978)
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.statified.mediaplayer?.pause()
                            SongPlayingFragment.statified.playpause?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {

                }
            }
        }
    }
}