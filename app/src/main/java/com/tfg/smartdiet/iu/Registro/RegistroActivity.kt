package com.tfg.smartdiet.iu.Registro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tfg.smartdiet.databinding.ActivityRegistroBinding
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityRegistroBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.InicioSesionBtnEntrar.setOnClickListener {
            registrarUsuario()

        }

    }

    private fun registrarUsuario() {
        val etNombre = binding.InicioSesionEtUsuario.editText?.text.toString()
        val etEmail = binding.InicioSesionEtEmail.editText?.text.toString()
        val etContrasena = binding.InicioSesionEtContrasena.editText?.text.toString()
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(etEmail, etContrasena).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val i = Intent(this, MainActivity::class.java)
                startActivity(i)

            } else {
                Log.i("Registro", "${task.result}")
            }

        }.addOnFailureListener {
            Log.e("Registro", "${it.message}")
        }


        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}