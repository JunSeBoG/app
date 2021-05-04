package com.alcanosesp.appalcanos.ui.menulateral.solicitudes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterClickRV
import com.alcanosesp.appalcanos.model.ModeloLista

class SolicitudesFragment : Fragment(), AdapterClickRV.OnOpcionListener {

    private lateinit var adaptadorRV: AdapterClickRV

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_solicitudes, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.solicitudes_rv)

        adaptadorRV = AdapterClickRV(context, this)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptadorRV

        adaptadorRV.crearListaElementos(llenarlistaSolicitudes())
        adaptadorRV.notifyDataSetChanged()

        return view
    }

    override fun onOpcClick(position: Int) {
        when (position) {
            0 -> {
                this.findNavController()
                    .navigate(R.id.action_solicitudesFragment_to_beneficiosFragment)
            }
            1 -> {
                this.findNavController()
                    .navigate(R.id.action_solicitudesFragment_to_permisoListaFragment)
            }
            2 -> {
                this.findNavController()
                    .navigate(R.id.action_nav_solicitudes_to_cesantiasFragment)
            }
            3 -> {
                this.findNavController()
                    .navigate(R.id.action_nav_solicitudes_to_vacacionesFragment)
            }
        }
    }

    private fun llenarlistaSolicitudes(): ArrayList<ModeloLista> {
        val lista = ArrayList<ModeloLista>()

        lista.apply {
            add(
                ModeloLista(
                    R.drawable.ic_trending_up,
                    "Solicitud de beneficios",
                    "Accede para visualizar o registrar solicitudes de beneficios corporativos.",
                    "Beneficios"
                )
            )

            add(
                ModeloLista(
                    R.drawable.ic_speaker_notes,
                    "Solicitud de permisos",
                    "Accede para visualizar o registrar solicitudes de permisos.",
                    "Permisos"
                )
            )
            add(
                ModeloLista(
                    R.drawable.ic_monetization_onpx,
                    "Solicitud de cesantías",
                    "Accede para visualizar o registrar solicitudes de cesantías.",
                    "Cesantias"
                )
            )
            add(
                ModeloLista(
                    R.drawable.ic_beach_access,
                    "Solicitud de Vacaciones",
                    "Accede para visualizar o registrar solicitudes de vacaciones.",
                    "Vacaciones"
                )
            )
        }
        return lista
    }
}
