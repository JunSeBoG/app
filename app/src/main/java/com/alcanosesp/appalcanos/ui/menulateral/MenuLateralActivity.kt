package com.alcanosesp.appalcanos.ui.menulateral

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.NavHeaderMenuLateralBinding
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.ui.menulateral.incapacidades.IncapacidadViewModel
import com.alcanosesp.appalcanos.ui.menulateral.perfil.PerfilActivity
import com.alcanosesp.appalcanos.utils.stringToBitMap
import com.alcanosesp.appalcanos.utils.validarSolicitarPermiso
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_inicio.*
import org.json.JSONObject


class MenuLateralActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(IncapacidadViewModel::class.java)
    }
    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private var funcionarioFoto: String = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: NavHeaderMenuLateralBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmFuncionario.obtenerFuncionario()
Log.i("si llega","pedir")
        validarSolicitarPermiso(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        setContentView(R.layout.activity_menu_lateral)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.nav_header_menu_lateral, navView, false
        )

        navView.addHeaderView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        //val imgFuncionario = findViewById<CircleImageView>(R.id.imgFuncionario)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio, R.id.nav_certificados_desprendibles, R.id.nav_consultas,
                R.id.nav_datos_personales, R.id.nav_incapacidades, R.id.nav_solicitudes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        vmFuncionario.funcionario.observe(this, Observer {
            binding.funcionario = it
            funcionarioFoto = it.foto.toString()
            App.idFuncionario = it.id.toInt()

            resgitarEscuchaNotificacion(it.numeroDocumento!!)

            val imgFuncionario = findViewById<CircleImageView>(R.id.imgFuncionario)
            if (funcionarioFoto.isNotEmpty()) {
                val imagenBitmap = stringToBitMap(funcionarioFoto)
                imgFuncionario.setImageBitmap(imagenBitmap)
            }
        })






    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun irA(v: View) {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun resgitarEscuchaNotificacion(identificacion: String) {
        App.ccFuncionario = identificacion
        (application as App).socket.emit("mobile:register", object : JSONObject() {
            init {
                put("userId", identificacion)
            }
        })
    }
}
