package com.alcanosesp.appalcanos.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.RecyclerViewStandardItemBinding
import com.alcanosesp.appalcanos.db.entity.*
import com.alcanosesp.appalcanos.model.Desprendible
import com.alcanosesp.appalcanos.model.InterrupcionVacaciones
import com.alcanosesp.appalcanos.model.RecyclerViewStandard
import com.alcanosesp.appalcanos.utils.*

class AdapterRecyclerView(private val contexto: Context?, private var mListener: OnRecyclerClickListener) : RecyclerView.Adapter<AdapterRecyclerView.ViewHolderRV>() {

    private var listaElementos = ArrayList<Any>()

    inner class ViewHolderRV(
        private val binding: RecyclerViewStandardItemBinding,
        private val listener: OnRecyclerClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener  {

        var objeto: Any? = null
        fun agregarItem(objeto: Any, posicion: Int) {
            this.objeto = objeto

            var item: Any
            var clase: RecyclerViewStandard? = null
            var color: Int? = 0

            when (objeto) {
                is Familiar -> {
                    item = objeto as Familiar
                    clase = RecyclerViewStandard(
                        item.primerNombre!!.substring(0,1) + "" + item.primerApellido!!.substring(0,1),
                        item.primerNombre + " " + item.primerApellido,
                        item.parentescoNombre!!,
                        "${item.edad} Años",
                        item.estado!!
                    )
                    color = contexto?.getColor(estadosInformacion[clase.estado]!!)
                }
                is Estudio -> {
                    item =  objeto as Estudio
                    clase = RecyclerViewStandard(
                        posicion.plus(1).toString(),
                        item.titulo!!,
                        item.nivelEducativoNombre!!,
                        item.fechaFin!!,
                        item.estado!!
                    )
                    color = contexto?.getColor(estadosInformacion[clase.estado]!!)
                }
                is Experiencia -> {
                    item = objeto as Experiencia
                    clase = RecyclerViewStandard(
                        posicion.plus(1).toString(),
                        item.cargo,
                        item.empresa!!,
                        item.fechaFin!!,
                        item.estado!!
                    )
                    color = contexto?.getColor(estadosInformacion[clase.estado]!!)
                }

                is SolicitudBeneficio -> {
                    item = objeto as SolicitudBeneficio
                    clase = RecyclerViewStandard(
                        posicion.plus(1).toString(),
                        item.tipoBeneficioNombre!!,
                        "",
                        item.fechaSolicitud!!,
                        item.estado!!
                    )

                    binding.subTituloRecycler.visibility = View.GONE
                    color = contexto?.getColor(estadosBeneficios[clase.estado]!!)
                    clase.estado = item.estado(item.estado!!)
                }

                is SolicitudPermiso -> {
                    item = objeto as SolicitudPermiso
                    clase = RecyclerViewStandard(
                        posicion.plus(1).toString(),
                        item.tipoAusentismoNombre!!,
                        "",
                        item.fechaInicio!!,
                        item.estado!!
                    )

                    binding.subTituloRecycler.visibility = View.GONE
                    color = contexto?.getColor(estadosPermisos[clase.estado]!!)
                }

                is SolicitudCesantia -> {
                    item = objeto as SolicitudCesantia
                    clase = RecyclerViewStandard(
                        posicion = posicion.plus(1).toString(),
                        titulo = item.motivoSolicitudCesantiaNombre,
                        descripcion = item.fechaSolicitud,
                        estado = estadosBeneficiosNombres[item.estado]!!
                    )
                    binding.subTituloRecycler.visibility = View.GONE
                    color= contexto?.getColor(estadosBeneficios[item.estado]!!)
                }

                is SolicitudVacaciones -> {
                    item = objeto as SolicitudVacaciones
                    clase = RecyclerViewStandard(
                        posicion = posicion.plus(1).toString(),
                        titulo = item.fechaInicioDisfrute.substring(0,4),
                        descripcion = item.fechaInicioDisfrute!!,
                        estado = estadosBeneficiosNombres[item.estado]!!
                    )
                    binding.subTituloRecycler.visibility = View.GONE
                    color= contexto?.getColor(estadosVacaciones[item.estado]!!)
                }
                is InterrupcionVacaciones -> {
                    item = objeto as InterrupcionVacaciones
                    clase = RecyclerViewStandard(
                        posicion = posicion.plus(1).toString(),
                        titulo =  item.causalInterrupcion,
                        subTitulo = "Inicio: "+item.fechaInicio,
                        descripcion = "Fin: "+ item.fechaFin
                    )

                    binding.estadoRecycler.isVisible = false
                    color= listaColores[clase.posicion.toInt()]
                }
                is Desprendible -> {
                    item = objeto as Desprendible
                    clase = RecyclerViewStandard(
                        posicion = posicion.plus(1).toString(),
                        titulo = item.anioDesprendible!!,
                        descripcion = item.mesDesprendible!!,
                        estado = item.subPeriodoDesprendible!!
                    )

                    binding.subTituloRecycler.visibility = View.GONE
                    color = contexto?.getColor(listaColores[posicion])
                }
            }

            binding.recycler = clase
            color?.let{ binding.posicionRecycler.background.setTint(it)
                        binding.estadoRecycler.background.setTint(it) }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.seleccionarItem(objeto)
        }
    }

    interface OnRecyclerClickListener {
        fun seleccionarItem(item: Any?)
    }

    fun crearListaElementos(lista: Any) {
        listaElementos = lista as ArrayList<Any>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRV {
        val inflater = LayoutInflater.from(contexto)
        val binding: RecyclerViewStandardItemBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.recycler_view_standard_item,
            parent,
            false
        )
        return ViewHolderRV(binding, mListener)
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolderRV, position: Int) {
        val estudio = listaElementos[position]
        holder.agregarItem(estudio, position)
    }
}