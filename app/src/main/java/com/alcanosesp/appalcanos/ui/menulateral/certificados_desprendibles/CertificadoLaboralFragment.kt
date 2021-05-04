package com.alcanosesp.appalcanos.ui.menulateral.certificados_desprendibles

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.MimeTypeFilter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRvCertificadoLaboral
import com.alcanosesp.appalcanos.model.CertificadoLaboral
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import kotlinx.android.synthetic.main.fragment_certificado_laboral.*


class CertificadoLaboralFragment : Fragment(),
    AdapterRvCertificadoLaboral.OnCertificadoLaboralListener {

    private lateinit var funcionarioId: String

    private lateinit var recyclerView:RecyclerView
    private lateinit var downloadListener: DownloadListener

    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private lateinit var adaptadorRV: AdapterRvCertificadoLaboral

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_certificado_laboral, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.rvCertificadolaboral)

        adaptadorRV = AdapterRvCertificadoLaboral(context!!, this)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptadorRV

        //myWebView = view.findViewById(R.id.webview)

        obteberFuncionario()
        return view
    }

    override fun onCertificadoClick(position: Int) {
        val pdf ="http://drive.google.com/viewerng/viewer?embedded=true&url="
        val urll = llenarListaCertificados()[position].URL
        val pilaIntent =
          Intent(Intent.ACTION_VIEW, Uri.parse(pdf+llenarListaCertificados()[position].URL))
        startActivity(pilaIntent)

       //myWebView.loadUrl("llenarListaCertificados()[position].URL")
    }

    private fun llenarListaCertificados(): ArrayList<CertificadoLaboral> {

        val lista = ArrayList<CertificadoLaboral>()

        val url: String = getString(R.string.apiNomina)

        lista.add(
            0, CertificadoLaboral(
                R.drawable.ic_account_box,
                "Certificado laboral donde solo se muestre el cargo.",
                R.drawable.ic_save_alt,
                url.plus("api/Certificados/${funcionarioId}/cargo")
            )
        )

        lista.add(
            CertificadoLaboral(
                R.drawable.ic_monetization_onpx,
                "Certificado laboral donde solo se muestre el sueldo.",
                R.drawable.ic_save_alt,
                url.plus("api/Certificados/${funcionarioId}/sueldo")
            )
        )

        lista.add(
            CertificadoLaboral(
                R.drawable.ic_business_centerpx,
                "Certificado laboral donde se muestre el cargo y el sueldo.",
                R.drawable.ic_save_alt,
                url.plus("api/Certificados/${funcionarioId}/sueldocargo")
            )
        )
        return lista
    }

    private fun obteberFuncionario() {
        vmFuncionario.obtenerFuncionario()
        vmFuncionario.funcionario.observe(this, Observer {

            funcionarioId = it.id
            adaptadorRV.crearListaCertificados(llenarListaCertificados())
            adaptadorRV.notifyDataSetChanged()
        })
    }
}
