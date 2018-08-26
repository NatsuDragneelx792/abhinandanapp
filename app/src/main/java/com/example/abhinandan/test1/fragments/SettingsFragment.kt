package com.example.abhinandan.test1.fragments


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.example.abhinandan.test1.R



class SettingsFragment : android.support.v4.app.Fragment() {

    var myactivity : Activity? = null
    var shakeswitch : Switch? = null
    object static{
        var MY_SHAKE_PREF = "ShakeFeature"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = "Settings"
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        shakeswitch = view?.findViewById(R.id.switchshake)
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
        val prefs = myactivity?.getSharedPreferences(static.MY_SHAKE_PREF, Context.MODE_PRIVATE)
        val isallowed = prefs?.getBoolean("feature",false)
        if(isallowed as Boolean){
            shakeswitch?.isChecked = true
        }else{
            shakeswitch?.isChecked = false
        }

        shakeswitch?.setOnCheckedChangeListener({compoundButton, b ->
            if(b){
                val editor = myactivity?.getSharedPreferences(static.MY_SHAKE_PREF, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
            }else{
                val editor = myactivity?.getSharedPreferences(static.MY_SHAKE_PREF, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.apply()
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


}
