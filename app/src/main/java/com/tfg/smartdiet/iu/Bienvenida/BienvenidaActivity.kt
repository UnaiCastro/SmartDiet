package com.tfg.smartdiet.iu.Bienvenida

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.tfg.smartdiet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.ActivityBienvenidaBinding
//import com.tfg.smartdiet.databinding.ActivityMainBinding
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity

//import com.tfg.smartdiet.iu.PaginaPrincipal.PrimeraPantalla.PrincipalFragment

import com.tfg.smartdiet.iu.Registro.RegistroActivity

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBienvenidaBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding =
            ActivityBienvenidaBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        val conf = ConfigUsuario(this.applicationContext.getSharedPreferences("Configuracion", Context.MODE_PRIVATE))
        conf.initTema()
        conf.initIdioma(this)
        if (!conf.sesionAbierta()) {
            initListener()
        }
        else{
            abiertaListener()
        }
    }

    private fun initListener() {
        binding.BienvenidaBtnRegistro.setOnClickListener {
            val i = Intent(this,RegistroActivity::class.java)
            startActivity(i)
        }

        binding.BienvenidaBtnInicioSesion.setOnClickListener {
            auth= FirebaseAuth.getInstance()
            if (auth.currentUser!=null){
                val i =Intent(this,MainActivity::class.java)
                startActivity(i)
            }else{
                val i = Intent(this,InicioSesionActivity::class.java)
                startActivity(i)
            }

        }
    }

    private fun abiertaListener(){
        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
    }


}