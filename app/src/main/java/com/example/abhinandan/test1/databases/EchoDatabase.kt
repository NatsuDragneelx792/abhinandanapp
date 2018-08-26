package com.example.abhinandan.test1.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.abhinandan.test1.Songs
import com.example.abhinandan.test1.databases.EchoDatabase.staticated.TABLE_NAME

class EchoDatabase : SQLiteOpenHelper{

    var _songlist = ArrayList<Songs>()
    object staticated{
        val TABLE_NAME = "FavouriteTable"
        val COLUMN_ID = "SongId"
        val COLUMN_SONGTITLE = "SongTitle"
        val COLUMN_SONGARTIST = "SongArtist"
        val COLUMN_SONGPATH = "SongPath"
        var DB_VERSION = 1
        val DB_NAME = "FavouriteDatabase"
    }

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL("CREATE TABLE " + staticated.TABLE_NAME + "( " + staticated.COLUMN_ID + " INTEGER," + staticated.COLUMN_SONGARTIST + " STRING," +
                staticated.COLUMN_SONGTITLE + " STRING," + staticated.COLUMN_SONGPATH + " STRING);")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, staticated.DB_NAME, null, staticated.DB_VERSION )

    fun storeasfavourite(id : Int?,artist : String?,title : String?,path : String?){
        val db = this.writableDatabase
        var contentvalues = ContentValues()
        contentvalues.put(staticated.COLUMN_ID,id)
        contentvalues.put(staticated.COLUMN_SONGARTIST,artist)
        contentvalues.put(staticated.COLUMN_SONGTITLE,title)
        contentvalues.put(staticated.COLUMN_SONGPATH,path)
        db.insert(TABLE_NAME,null,contentvalues)
        db.close()
    }

    fun querydblist() : ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            var query = "SELECT * FROM " + staticated.TABLE_NAME
            var csor = db.rawQuery(query, null)
            if (csor.moveToFirst()) {
                do {
                    var _id = csor.getInt(csor.getColumnIndexOrThrow(staticated.COLUMN_ID)).toLong()
                    var _artist = csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONGARTIST))
                    var _title = csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONGTITLE))
                    var _path = csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONGPATH))
                    _songlist.add(Songs(_id , _title, _artist, _path, 0))
                } while (csor.moveToNext())
            } else {
                return null
            }
        }catch(e : Exception){
            e.printStackTrace()
        }
        return _songlist
    }

    fun checkifidexists(_title : String?,_artist : String?) : Boolean{
        var flag = false
        var db = this.readableDatabase
        var query: String? = null
        try {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE SongTitle = '$_title' AND SongArtist = '$_artist'"
        var csor = db.rawQuery(query,null)
        if(csor.moveToFirst()){
            do{
                flag = true
            }while(csor.moveToNext())
        }else{
            return false
        }
        }catch(e : Exception){
            e.printStackTrace()
        }
        db.close()
        return flag
    }

    fun deletefavourite( _title : String?,_artist : String?){
        val db = this.writableDatabase
        try {
            var query = "DELETE FROM " + TABLE_NAME + " WHERE SongTitle = '$_title' AND SongArtist = '$_artist'"
            db.execSQL(query)
        }catch (e : Exception){
            e.printStackTrace()
        }
        db.close()
    }

    fun checksize() : Int {
        var returnthis : Int = 0
        var db = this.readableDatabase
        var query = "SELECT * FROM " + staticated.TABLE_NAME
        var csor = db.rawQuery(query,null)
        if(csor.moveToFirst()){
            do{
                returnthis = returnthis + 1

            }while(csor.moveToNext())
        }else{
            return 0
        }
        return returnthis
    }


}