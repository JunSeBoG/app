package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.ItemSpinner

class SpinnerAdapter (context: Context) :
    ArrayAdapter<ItemSpinner>(context, R.layout.spinner_adapter) {

    private var items = mutableListOf<ItemSpinner>()

    fun setItems(items: List<ItemSpinner>) {
        synchronized(this) {
            this.items.clear()
            this.items.add(ItemSpinner(0,""))

            for (i in items.indices) {
                this.items.add(items[i])
            }

            //this.items = items
            notifyDataSetChanged()
        }
    }

    override fun getPosition(item: ItemSpinner?): Int {
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
                .inflate(R.layout.spinner_adapter, parent, false)
        }

        val itemSpinner: ItemSpinner? = getItem(position)

        val nombre = view?.findViewById<TextView>(R.id.nombre_tipo_vivienda)
        nombre!!.text = itemSpinner?.nombre

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (convertView == null){
            view = LayoutInflater.from(context)
                .inflate(R.layout.dropdown_items, parent, false)
        }

        val itemSpinner: ItemSpinner? = getItem(position)

        val nombre = view?.findViewById<TextView>(R.id.nombre_elemento)
        nombre!!.text = itemSpinner?.nombre

        return view!!
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): ItemSpinner? {
        return items[position]
    }

    fun obtenerPosicionValor(item: ItemSpinner?): Int{
        var posicion = 0
        for (i in items.indices) {
            if (item?.id == items[i].id){
                posicion = i
                break
            }
        }
        return posicion
    }

    fun obtenerPosicionNombre(s: String?): Int{
        var posicion = 0
        for (i in items.indices) {
            if (s.equals(items[i].nombre)){
                posicion = i
                break
            }
        }
        return posicion
    }
}