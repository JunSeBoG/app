package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.SoporteSolicitudPermisoItemRvBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte

class AdapterRVSoportePermiso(
    private val context: Context,
    private val mlistener: OnSoportePermisosListener,
    val icono: String? = null
) : RecyclerView.Adapter<AdapterRVSoportePermiso.ViewHolder>() {

    private var listaSoportePermisos = ArrayList<SolicitudPermisoSoporte>()

    inner class ViewHolder(
        private val binding: SoporteSolicitudPermisoItemRvBinding,
        private val listener: OnSoportePermisosListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var soportePermiso: SolicitudPermisoSoporte? = null

        fun agregarItem(soportePermiso: SolicitudPermisoSoporte) {
            this.soportePermiso = soportePermiso
            binding.soportepermiso = soportePermiso

            if (icono != null) {
                binding.iconoAccionSolicitudPermisoSoporte.setImageResource(R.drawable.ic_delete_outline)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.seleccionarSoportePermiso(soportePermiso)
        }

    }

    interface OnSoportePermisosListener {
        fun seleccionarSoportePermiso(soportePermiso: SolicitudPermisoSoporte?)
    }

    fun crearListaElementos(lista: ArrayList<SolicitudPermisoSoporte>) {
        listaSoportePermisos = lista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: SoporteSolicitudPermisoItemRvBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.soporte_solicitud_permiso_item_rv,
                parent,
                false
            )
        return ViewHolder(binding, mlistener)
    }

    override fun getItemCount(): Int {
        return listaSoportePermisos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soportePermiso = listaSoportePermisos[position]
        holder.agregarItem(soportePermiso)
    }
}