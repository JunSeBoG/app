package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.ModeloLista
import com.alcanosesp.appalcanos.model.CertificadoLaboral
import kotlinx.android.synthetic.main.datos_personales_item_root_click_rv.view.*
import java.util.*

class AdapterClickRV(private val contexto: Context?, private var mListener: OnOpcionListener) :
    RecyclerView.Adapter<AdapterClickRV.ViewHolderRV>() {

    private var listaElementos = ArrayList<ModeloLista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRV {
        val vistaRoot = LayoutInflater.from(contexto)
            .inflate(R.layout.datos_personales_item_root_click_rv, parent, false)

        return ViewHolderRV(vistaRoot, mListener)
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolderRV, position: Int) {
        val informacion = listaElementos[position]
        holder.agregarItem(informacion)
    }

    fun crearListaElementos(lista: ArrayList<ModeloLista>) {
        listaElementos = lista
    }

    inner class ViewHolderRV(itemView: View, private val listener: OnOpcionListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun agregarItem(info: ModeloLista) {

            itemView.iv_c_principal.setImageResource(info.img)
            itemView.txt_c_inicial.text = info.titulo
            itemView.txt_c_final.text = info.descripcion
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onOpcClick(adapterPosition)
        }
    }

    interface OnOpcionListener {
        fun onOpcClick(position: Int)
    }
}
