package com.example.abhinandan.test1.adapters

import android.content.Context
import android.support.constraint.R.id.parent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.abhinandan.test1.R
import com.example.abhinandan.test1.activities.MainActivity
import com.example.abhinandan.test1.fragments.AboutUsFragment
import com.example.abhinandan.test1.fragments.FavouriteFragment
import com.example.abhinandan.test1.fragments.MainScreenFragment
import com.example.abhinandan.test1.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList : ArrayList<String>, _getImages : IntArray,_context : Context):
        RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){

    var contentList : ArrayList<String>?= null
    var getImages : IntArray?= null
    var con : Context?= null

    init{
        this.contentList = _contentList
        this.getImages = _getImages
        this.con = _context
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavViewHolder {
       var itemview = LayoutInflater.from(p0?.context)
                .inflate(R.layout.row_custom_navigationdrawer,p0,false)
        val returnthis = NavViewHolder(itemview)

        return returnthis
    }

    override fun getItemCount(): Int {
        return contentList?.size as Int
    }

    override fun onBindViewHolder(p0: NavViewHolder, p1: Int) {

        p0?.Icon_get?.setBackgroundResource(getImages?.get(p1)as Int)
        p0?.Text_GET?.setText(contentList?.get(p1))
        p0?.contentholder?.setOnClickListener({
            if(p1==0){
                val mainscreenfragment = MainScreenFragment()
                (con as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment,mainscreenfragment).commit()
                MainActivity.staticfied.drawer?.closeDrawers()
            }
            else if(p1==1){
                val favoutitefragment = FavouriteFragment()
                (con as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment,favoutitefragment).commit()
                MainActivity.staticfied.drawer?.closeDrawers()
            }
            else if(p1==2){
                val settingsfragment = SettingsFragment()
                (con as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment,settingsfragment).commit()
                MainActivity.staticfied.drawer?.closeDrawers()
            }else{
                val aboutusfragment = AboutUsFragment()
                (con as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment,aboutusfragment).commit()
                MainActivity.staticfied.drawer?.closeDrawers()
            }
        })

        MainActivity.staticfied.drawer?.closeDrawers()

    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Icon_get: ImageView?= null
        var Text_GET: TextView?= null
        var contentholder : RelativeLayout?= null

        init{
            Icon_get = itemView?.findViewById(R.id.icon_navdrawer)
            Text_GET = itemView?.findViewById(R.id.text_navdrawer)
            contentholder = itemView?.findViewById(R.id.navdrawer_item_Content)

        }


    }

}