package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.IncapacidadItemRvBinding
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario
import com.alcanosesp.appalcanos.utils.estadosAusentismoFuncionario
import com.alcanosesp.appalcanos.utils.estadosInformacion

class AdapterRVIncapacidad(
    private val contexto: Context?,
    private val mlistener: OnIncapacidadListener
) : RecyclerView.Adapter<AdapterRVIncapacidad.ViewHolderRv>() {

    private var listaElementos = ArrayList<AusentismoFuncionario>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRv {
        val inflater = LayoutInflater.from(contexto)
        val binding: IncapacidadItemRvBinding =
            DataBindingUtil.inflate(inflater, R.layout.incapacidad_item_rv, parent, false)
        return  ViewHolderRv(binding, mlistener)

    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolderRv, position: Int) {
        val incapacidad = listaElementos[position]
        holder.agregarItem(incapacidad, position)
    }

    fun crearListaElementos(lista: ArrayList<AusentismoFuncionario>) {
        listaElementos = lista
    }
    inner class ViewHolderRv(
        private val binding: IncapacidadItemRvBinding,
        private val listener: OnIncapacidadListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var incapacidad: AusentismoFuncionario? = null

        fun agregarItem(incapacidad: AusentismoFuncionario, Position: Int) {
            this.incapacidad = incapacidad
            binding.incapacidad = incapacidad

            contexto?.getColor(estadosAusentismoFuncionario[incapacidad.estado]!!)
                ?.let {
                    binding.tvCirculoIncapacidad.background.setTint(it)
                    binding.tvEstadoNotificacion.background.setTint(it)
                }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.seleccionarIncapacidad(incapacidad)
        }
    }

    interface OnIncapacidadListener {
        fun seleccionarIncapacidad(incapacidad: AusentismoFuncionario?)
    }
}