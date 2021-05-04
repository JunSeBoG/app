package com.alcanosesp.appalcanos.ui.menulateral.inicio

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.FragmentInicioBinding
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.utils.estadosBeneficios
import com.alcanosesp.appalcanos.utils.estadosBeneficiosNombres
import com.alcanosesp.appalcanos.utils.estadosPermisos
import com.alcanosesp.appalcanos.utils.stringToBitMap
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_inicio.*
import kotlinx.android.synthetic.main.graficas_legenda.view.*

class InicioFragment : Fragment() {
    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private val vmGraficas by lazy {
        ViewModelProviders.of(this).get(InicioViewModel::class.java)
    }
    private lateinit var binding: FragmentInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //vmFuncionario.validarUltimaFechaActualizacion()
        vmFuncionario.obtenerFuncionario()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inicio, container, false)


        cargarImagen()


        binding.ivMisDatos.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_datos_personales)
        }

        binding.ivConsulta.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_consultas)
        }

        binding.ivIncapacidades.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_incapacidades)
        }

        binding.ivSolicitudes.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_solicitudes)
        }

        binding.ivFotoInicio.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_perfilActivity)
        }

        return binding.root
    }

    private fun graficasBeneficios() {
        vmGraficas.inicioGraficasBeneficios.observe(viewLifecycleOwner, Observer {
            val total = it.sumBy { it.valor }
            if (total > 0) {
                llLegenBeneficiosIzquierda.removeAllViews()
                llLegenBeneficiosDerecha.removeAllViews()
                val colores = IntArray(it.size)
                var posicion = "izquierda"
                val benficioEntry = ArrayList<PieEntry>()


                for (i in it.indices) {

                    val valor = (it[i].valor * 100) / total
                    val descripcion = it[i].descripcion.toString().trim()
                    val colorEstado = estadosBeneficios[descripcion]!!
                    val formatDescripcion = estadosBeneficiosNombres[descripcion]

                    colores[i] = colorEstado
                    benficioEntry.add(PieEntry(valor.toFloat(), "$valor%"))


                    val inflater = layoutInflater
                    if (posicion == "izquierda") {
                        val tabla = inflater.inflate(
                            R.layout.graficas_legenda,
                            llLegenBeneficiosIzquierda,
                            false
                        )
                        llLegenBeneficiosIzquierda.addView(tabla)

                        tabla.icono.background.setTint(context!!.getColor(colorEstado))
                        tabla.texto.text = formatDescripcion
                        tabla.texto.setTextColor(context!!.getColor(colorEstado))

                        posicion = "derecha"
                    } else {
                        val tabla =
                            inflater.inflate(
                                R.layout.graficas_legenda,
                                llLegenBeneficiosDerecha,
                                false
                            )
                        llLegenBeneficiosDerecha.addView(tabla)
                        tabla.icono.background.setTint(context!!.getColor(colorEstado))
                        tabla.texto.text = formatDescripcion
                        tabla.texto.setTextColor(context!!.getColor(colorEstado))

                        posicion = "izquierda"
                    }
                }

                val beneficioDataset = PieDataSet(benficioEntry, "").apply {
                    setColors(colores, context)
                    setDrawValues(false)
                    sliceSpace = 1f//espacio entre datos
                }

                val beneficiodata = PieData(beneficioDataset)
                val beneficioChar = view?.findViewById(R.id.pcBeneficiosInicio) as PieChart
                beneficioChar.data = beneficiodata
                beneficioChar.animateX(2200)
                beneficioChar.setEntryLabelTextSize(14f)
                beneficioChar.setEntryLabelTypeface(
                    ResourcesCompat.getFont(
                        context!!,
                        R.font.muli_regular
                    )
                )
                beneficioChar.setNoDataText("Error al cargar datos")  //En caso de no cargar el Grafico
                beneficioChar.description.text = ""
                beneficioChar.transparentCircleRadius = 0f
                beneficioChar.holeRadius = 1f //Tamaño del circulo

                beneficioChar.invalidate()

                val legend = beneficioChar.legend
                legend.isEnabled = false
                legend.isWordWrapEnabled = true

                if (total > 0) binding.llGraficaBeneficios.isVisible = true
            }
        })
    }

    private fun graficaPermisos() {
        vmGraficas.inicioGraficasPermisos.observe(viewLifecycleOwner, Observer {
            val total = it.sumBy { it.valor }
            if (total > 0) {
                llLegenPermisosIzquierda.removeAllViews()
                llLegenPermisosDerecha.removeAllViews()

                val datos = BarData()
                var posicion = "izquierda"

                for (i in it.indices) {
                    val entradas = ArrayList<BarEntry>()

                    val valor = (it.get(i).valor * 100) / total
                    val descripcion = it[i].descripcion.toString().trim()
                    val formatDescripcion = estadosBeneficiosNombres[descripcion]

                    val colorEstado = estadosPermisos[descripcion]!!

                    entradas.add(BarEntry(i.toFloat(), valor.toFloat()))

                    val barDataSet = BarDataSet(entradas, descripcion).apply {
                        setColors(intArrayOf(colorEstado), context)
                        setDrawValues(false)
                    }

                    datos.addDataSet(barDataSet)
                    if (posicion == "izquierda") {
                        val inflater = layoutInflater
                        val tabla =
                            inflater.inflate(
                                R.layout.graficas_legenda,
                                llLegenPermisosIzquierda,
                                false
                            )
                        llLegenPermisosIzquierda.addView(tabla)
                        tabla.icono.background.setTint(context!!.getColor(colorEstado))
                        tabla.texto.text = formatDescripcion
                        tabla.texto.setTextColor(context!!.getColor(colorEstado))

                        posicion = "derecha"
                    } else {
                        val inflater = layoutInflater
                        val tabla =
                            inflater.inflate(
                                R.layout.graficas_legenda,
                                llLegenPermisosDerecha,
                                false
                            )
                        llLegenPermisosDerecha.addView(tabla)
                        tabla.icono.background.setTint(context!!.getColor(colorEstado))
                        tabla.texto.text = formatDescripcion
                        tabla.texto.setTextColor(context!!.getColor(colorEstado))

                        posicion = "izquierda"
                    }
                }

                val permisoBar = view?.findViewById<BarChart>(R.id.bcPermisosInicio) as BarChart

                permisoBar.data = datos
                permisoBar.description?.text = ""
                permisoBar.setDrawValueAboveBar(true)
                permisoBar.setFitBars(true) //ajuste bar
                permisoBar.isHighlightPerDragEnabled = false // NO select bar defoult
                permisoBar.invalidate()
                permisoBar.apply {
                }
                val left: YAxis = permisoBar.axisLeft
                val rigth: YAxis = permisoBar.axisRight

                //Quita la cuadricula
                //left.setDrawGridLines(false)
                //permisoBar.xAxis.setDrawGridLines(false)
                //rigth.setDrawGridLines(false)

                //Quita los numeros
                left.setDrawLabels(false)
                rigth.setDrawLabels(false)
                permisoBar.xAxis.setDrawLabels(false)

                //permisoBar.xAxis.setDrawAxisLine(false)

                //Quita las lineas
                left.setDrawAxisLine(false)
                rigth.setDrawAxisLine(false)

                permisoBar.rendererRightYAxis.paintAxisLabels.style = Paint.Style.FILL_AND_STROKE

                permisoBar.legend.isEnabled = false


                if (total > 0) binding.llGraficaPermisosIniico.isVisible = true
            }
        })
    }

    private fun graficaActualizacionDatos() {
        vmGraficas.inicioGraficasActualizarDatos.observe(viewLifecycleOwner, Observer {

            val actualizarEntry = ArrayList<PieEntry>().apply {
                add(PieEntry(it.valor.toFloat(), ""))
                add(PieEntry((100 - it.valor).toFloat(), ""))
            }

            val actualizarDataset = PieDataSet(actualizarEntry, "").apply {
                setColors(intArrayOf(R.color.morado_consulta, R.color.gris_grafica), context)
                setDrawValues(false)
                setGradientColor(R.color.coral, R.color.colorPrimaryDark)

            }

            val actualizaData = PieData()
            actualizaData.addDataSet(actualizarDataset)


            val pie = view?.findViewById(R.id.pcActualizarDatosInicio) as PieChart
            pie.data = actualizaData
            pie.animateX(2200)
            pie.centerText = "${it.valor}%"
            pie.setCenterTextColor(context!!.getColor(R.color.gris))
            pie.setCenterTextTypeface(ResourcesCompat.getFont(context!!, R.font.muli_regular))
            pie.setCenterTextSize(70f)
            pie.holeRadius = 90f  //Tamaño del circulo
            pie.setNoDataText("Error al cargar datos")  //En caso de no cargar el Grafico
            pie.description.text = ""
            pie.legend.isEnabled = false //Quitar indicadores
            pie.invalidate()

            pie.isVisible = isVisible
        })
    }

    private fun cargarImagen() {
        vmFuncionario.funcionario.observe(this, Observer {
            var funcionario = it
            if (funcionario != null) {
                vmGraficas.optenerDatosGraficasInicioAPI(context!!)

                binding.funcionario = funcionario
                var img = view?.findViewById<CircleImageView>(R.id.ivFotoInicio)

                if (!it.foto.isNullOrEmpty()) {
                    img?.setImageBitmap(stringToBitMap(it.foto!!))
                } else {
                    img?.setImageResource(R.drawable.empty_personaje)
                }


                graficaActualizacionDatos()
                graficasBeneficios()
                graficaPermisos()
            }
        })
    }
}
