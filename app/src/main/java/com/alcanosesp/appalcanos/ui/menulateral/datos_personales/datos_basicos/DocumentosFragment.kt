package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alcanosesp.appalcanos.R

/**
 * A simple [Fragment] subclass.
 */
class DocumentosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documentos, container, false)
    }


}
