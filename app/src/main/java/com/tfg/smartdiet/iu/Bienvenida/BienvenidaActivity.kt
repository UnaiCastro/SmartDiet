package com.tfg.smartdiet.iu.Bienvenida

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.ActivityBienvenidaBinding
import com.tfg.smartdiet.databinding.ActivityMainBinding
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity
import com.tfg.smartdiet.iu.Registro.RegistroActivity

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBienvenidaBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityBienvenidaBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
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
}