package com.alcanosesp.appalcanos.ui.menulateral.consultas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterClickRV
import com.alcanosesp.appalcanos.model.ModeloLista

class ConsultasFragment : Fragment(), AdapterClickRV.OnOpcionListener {

    private lateinit var adaptadorRV: AdapterClickRV

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_consultas, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.ib_consultas_rv)

        adaptadorRV = AdapterClickRV(context, this)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptadorRV

        adaptadorRV.crearListaElementos(llenarListaConsultas())
        adaptadorRV.notifyDataSetChanged()

        return view
    }

    override fun onOpcClick(position: Int) {
        /*var url =llenarListaConsultas()[position].URL
        if( url.isNullOrEmpty()){
            this.findNavController().navigate(R.id.action_nav_consultas_to_certificadoLaboralFragment3)
        }else{
            val pilaIntent = Intent(Intent.ACTION_VIEW, Uri.parse(llenarListaConsultas()[position].URL))
            startActivity(pilaIntent)
        }*/

        when(position){
            0 -> {
                val pilaIntent = Intent(Intent.ACTION_VIEW, Uri.parse(llenarListaConsultas()[position].URL))
                startActivity(pilaIntent)
            }

            1 ->{
                this.findNavController().navigate(R.id.action_nav_consultas_to_certificadoLaboralFragment3)
            }

            2->{
                this.findNavController().navigate(R.id.action_nav_consultas_to_desprendiblesFragment)
            }
        }
    }

    private fun llenarListaConsultas(): ArrayList<ModeloLista> {
        val lista = ArrayList<ModeloLista>()

        lista.add(
            ModeloLista(
                R.drawable.ic_local_hospital,
                getString(R.string.consulta_pila_titulo),
                getString(R.string.consulta_pila_desc),
                getString(R.string.URLPagoPila)
            )
        )

        lista.add(
            ModeloLista(
                R.drawable.ic_work,
                getString(R.string.certificado_titulo),
                getString(R.string.certificado_descripcion),
                ""
            )
        )

        lista.add(
            ModeloLista(
                R.drawable.ic_description,
                "Desprendible de pago",
                "Genere un PDF del desprendible de pago de nómina.",
                ""
            )
        )
        return lista
    }
}
