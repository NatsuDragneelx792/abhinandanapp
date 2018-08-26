package com.example.abhinandan.test1.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.abhinandan.test1.R


class SplashActivity : AppCompatActivity() {

    var permissionString = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.PROCESS_OUTGOING_CALLS,
            android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(!permissiongrant(this@SplashActivity,*permissionString)){
            ActivityCompat.requestPermissions(this@SplashActivity,permissionString,132)

        }else{
            Handler().postDelayed({
                var startAct = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(startAct)
                this.finish()
            },1000)
        }
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            132->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[1]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[2]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[3]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[4]==PackageManager.PERMISSION_GRANTED ){
                    Handler().postDelayed({
                        var startAct = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(startAct)
                        this.finish()
                    },1000)
                    return
                }
                else{
                    Toast.makeText(this@SplashActivity,"Please grant all the permissions",Toast.LENGTH_SHORT).show()
                    this.finish()
                }

            }
            else->{
                Toast.makeText(this@SplashActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }

    }

    fun permissiongrant(context: Context, vararg permissionstring: String):Boolean{
        var hasallpermissions = true
        for( p in permissionstring){
            val res = context.checkCallingOrSelfPermission(p)
            if( res != PackageManager.PERMISSION_GRANTED){
                hasallpermissions=false
            }
        }
        return hasallpermissions
    }
}
