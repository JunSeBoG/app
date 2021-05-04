package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.RootOnBoardItemModel

class RootOnBoardItemAdapter (private val context : Context,
                              private val listItems : ArrayList<RootOnBoardItemModel>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rootItemOnBoardLayout : View = inflater.inflate(R.layout.item_root_onboard, null)

        val imgItem : ImageView = rootItemOnBoardLayout.findViewById(R.id.item_img_onboard)
        val tituloItem : TextView = rootItemOnBoardLayout.findViewById(R.id.item_tit_onboard)
        val descripcionItem : TextView = rootItemOnBoardLayout.findViewById(R.id.item_des_onboard)
        val containerItem : ConstraintLayout = rootItemOnBoardLayout.findViewById(R.id.item_cont_onboard)

        setViewItem(imgItem, tituloItem, descripcionItem, containerItem, position)

        container.addView(rootItemOnBoardLayout)

        return rootItemOnBoardLayout
    }

    /**
     *
     */
    private fun setViewItem(img : ImageView, titulo : TextView, des : TextView, container : ConstraintLayout, position : Int){
        img.setImageResource(listItems[position].itemImg)
        titulo.setText(listItems[position].itemTitulo)
        des.setText(listItems[position].itemDescripcion)
        container.setBackgroundColor(listItems[position].itemBgColor)
    }

    /**
     *
     */
    override fun getCount(): Int {
        return listItems.size
    }

    /**
     *
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    /**
     *
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
       return view == `object`
    }
}