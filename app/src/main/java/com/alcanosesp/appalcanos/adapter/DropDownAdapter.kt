package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.ItemAutocompletable


class DropDownAdapter(context: Context) :
    ArrayAdapter<ItemAutocompletable>(context, R.layout.dropdown_items) {

    private var items = mutableListOf<ItemAutocompletable>()

    fun setItems(items: List<ItemAutocompletable>) {
        synchronized(this) {
            this.items.add(ItemAutocompletable(0,""))

            for (i in items.indices) {
                this.items.add(items[i])
            }

            notifyDataSetChanged()
        }
    }

    override fun getPosition(item: ItemAutocompletable?): Int {
        var posicion = -1
        for (i in items.indices) {
            if (items[i].hashCode() == item.hashCode()) {
                posicion = i
                break
            }
        }
        return posicion
    }

    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup
    ): View {

        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.dropdown_items, parent, false)
        }

        val itemAutocompletable: ItemAutocompletable? = getItem(position)

        val nombre = view?.findViewById<TextView>(R.id.nombre_elemento)
        nombre!!.text = itemAutocompletable?.nombre

        return view!!
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): ItemAutocompletable? {
        return items[position]
    }

    fun obtenerPosicionValor(item: ItemAutocompletable?): Int{
        var posicion = 0
        for (i in items.indices) {
            if (item?.id == items[i].id){
                posicion = i
                break
            }
        }
        return posicion
    }
}