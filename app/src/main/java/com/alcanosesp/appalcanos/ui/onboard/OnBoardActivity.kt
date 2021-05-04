package com.alcanosesp.appalcanos.ui.onboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.RootOnBoardItemAdapter
import com.alcanosesp.appalcanos.ui.login.LoginActivity
import com.alcanosesp.appalcanos.model.RootOnBoardItemModel
import com.google.android.material.tabs.TabLayout

class OnBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        //window.navigationBarColor = getColor(R.color.blanco)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)

        val viewPager : ViewPager = findViewById(R.id.onboard_viewpager)
        val tabLayout : TabLayout = findViewById(R.id.onboard_tab)
        val btnAtras : ImageButton = findViewById(R.id.onboard_btn_atras)
        val btnSiguiente : ImageButton = findViewById(R.id.onboard_btn_siguiente)
        val btnComenzar : Button = findViewById(R.id.onboard_btn_comenzar)
        val listaItems : ArrayList<RootOnBoardItemModel> = ArrayList()

        viewPager.adapter =
            RootOnBoardItemAdapter(
                this,
                iniListaItems(listaItems)
            )

        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {btnComenzar.visibility = View.GONE
                          btnAtras.visibility = View.INVISIBLE
                          btnSiguiente.visibility = View.VISIBLE
                          tabLayout.visibility = View.VISIBLE}

                    listaItems.size.minus(1) -> {btnComenzar.visibility = View.VISIBLE
                                                        btnAtras.visibility = View.GONE
                                                        btnSiguiente.visibility = View.GONE
                                                        tabLayout.visibility = View.GONE
                                                        }

                    else -> {btnComenzar.visibility = View.GONE
                             btnAtras.visibility = View.VISIBLE
                             btnSiguiente.visibility = View.VISIBLE
                             tabLayout.visibility = View.VISIBLE}

                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

        btnAtras.setOnClickListener {
            if (viewPager.currentItem > 0){
                viewPager.currentItem --
            }
        }

        btnSiguiente.setOnClickListener {
            if (viewPager.currentItem < listaItems.size.minus(1)){
                viewPager.currentItem ++
            }
        }

        btnComenzar.setOnClickListener {
            val iLogin = Intent(this,LoginActivity::class.java)
            this.startActivity(iLogin)
            this.finish()
        }
    }

    /**
     *     Agrego los elementos que se van a mostrar en el OnBoarding
     */
    private fun iniListaItems(lista : ArrayList<RootOnBoardItemModel>) : ArrayList<RootOnBoardItemModel>{

        /*
            imagen,
            titulo,
            descripcion,
            color de fondo
        */


        lista.add(
            RootOnBoardItemModel(
                R.drawable.onboard_item_img_datos,
                R.string.onboard_tit_datos_personales,
                R.string.onboard_des_datos_personales,
                getColor(R.color.azul_m)
            )
        )

        lista.add(
            RootOnBoardItemModel(
                R.drawable.onboard_item_img_consultas,
                R.string.onboard_tit_consultas,
                R.string.onboard_des_consultas,
                getColor(R.color.azul_cobalto)
            )
        )

        lista.add(
            RootOnBoardItemModel(
                R.drawable.onboard_item_img_solicitudes,
                R.string.onboard_tit_solicitudes,
                R.string.onboard_des_solicitudes,
                getColor(R.color.rosa_ponche)
            )
        )

        return lista
    }
}
