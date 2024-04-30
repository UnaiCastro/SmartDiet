package com.tfg.smartdiet.iu.Registro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.ActivityRegistroBinding
import com.tfg.smartdiet.domain.Usuario
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityRegistroBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.RegistroBtnEntrar.setOnClickListener {
            registrarUsuario()

        }

    }

    private fun registrarUsuario() {
        val etNombre = binding.REgistroEtUsuario.editText?.text.toString()
        val etEmail = binding.RegistroEtEmail.editText?.text.toString()
        val etContrasena = binding.RegistroEtContrasena.editText?.text.toString()
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(etEmail, etContrasena).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                registrarUsuarioEnFirestore(etNombre,etEmail)
                val i = Intent(this, PreguntasRegistroActivity::class.java)
                startActivity(i)

            } else {
                Log.i("Registro", "${task.result}")
            }

        }.addOnFailureListener {
            Log.e("Registro", "${it.message}")
        }

    }

    private fun registrarUsuarioEnFirestore(
        etNombre: String,
        etEmail: String
    ) {

        db= FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val usuarioID = auth.currentUser?.uid
        val user = Usuario (
            userID = usuarioID.toString(),
            nombreUsuario = etNombre,
            correo = etEmail,
        ).toMap()

        db.collection("users").add(user).addOnSuccessListener {
            Log.i("Registro","Creado usuario $usuarioID")
        }.addOnFailureListener {
            Log.e("Registro","Creacion de Usuario $usuarioID fallida ${it.message}")
        }
    }
}