package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.CertificadoLaboral
import com.alcanosesp.appalcanos.utils.listaColores
import kotlinx.android.synthetic.main.certificadolaboral_item_root_click_rv.view.*
import java.util.*

class AdapterRvCertificadoLaboral   (
    private val contexto: Context,
    private val mlistener: OnCertificadoLaboralListener
) : RecyclerView.Adapter<AdapterRvCertificadoLaboral.ViewHolderRv>() {

    var listaElementos = ArrayList<CertificadoLaboral>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRv {
        val vista = LayoutInflater.from(contexto)
            .inflate(R.layout.certificadolaboral_item_root_click_rv, parent, false)

        return  ViewHolderRv(vista, mlistener)
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolderRv, position: Int) {
        val informacion = listaElementos[position]
        holder.agregarItem(informacion, position)
    }

    fun crearListaCertificados(lista: ArrayList<CertificadoLaboral  >) {
        listaElementos = lista
    }


    inner class ViewHolderRv(
        itemView: View, private val listener: OnCertificadoLaboralListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun agregarItem(certificadoLaboral: CertificadoLaboral, position: Int) {
            var posicion = position

            if (position > listaColores.size) {
                posicion = 0
            }

            itemView.imagen_principal.setImageResource (certificadoLaboral.img)

            contexto.getColor(listaColores[position])
                .let { itemView.findViewById<ImageView>(R.id.imagen_principal).background.setTint(it)}


            itemView.descripcion.text = certificadoLaboral.descripcion
            itemView.ivCertificadoLaboralSescargar.setImageResource(certificadoLaboral.icono)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onCertificadoClick(adapterPosition)
        }
    }

    interface OnCertificadoLaboralListener {
        fun onCertificadoClick(position: Int)
    }
}