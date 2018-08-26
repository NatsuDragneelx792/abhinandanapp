package com.example.abhinandan.test1

import android.os.Parcel
import android.os.Parcelable

class Songs(var Songid : Long, var songtiitle : String, var artist : String, var songdata : String, var songdate : Long)  : Parcelable{
    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    object Statified {
        var nameComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.songtiitle.toUpperCase()
            val songTwo = song2.songtiitle.toUpperCase()
            songOne.compareTo(songTwo)
        }
        var dateComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.songdate.toDouble()
            val songTwo = song2.songdate.toDouble()
            songTwo.compareTo(songOne)
        }
    }


}