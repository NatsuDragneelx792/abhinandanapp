package com.example.abhinandan.test1.fragments


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.abhinandan.test1.R



class AboutUsFragment : android.support.v4.app.Fragment() {

    var profile : ImageView? = null
    var contenthokder : RelativeLayout? = null
    var _line1 : TextView? = null
    var _line2 : TextView? = null
    var _line3 : TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = "About Us"
        var view = inflater.inflate(R.layout.fragment_about_us2, container, false)

        profile = view?.findViewById(R.id.profilephoto)
        contenthokder = view?.findViewById(R.id.textlayout)
        _line1 = view?.findViewById(R.id.line1)
        _line2 = view?.findViewById(R.id.line2)
        _line3 = view?.findViewById(R.id.line3)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


}
