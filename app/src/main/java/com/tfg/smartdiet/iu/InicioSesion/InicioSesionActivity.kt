package com.tfg.smartdiet.iu.InicioSesion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.tfg.smartdiet.databinding.ActivityInicioSesionBinding
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity
import com.tfg.smartdiet.iu.Registro.RegistroActivity

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityInicioSesionBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.InicioSesionBtnEntrar.setOnClickListener {
            signInAuth()
        }

        binding.InicioSesionTvTextoSubrayado.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInAuth() {
        auth = FirebaseAuth.getInstance()
        val correoUsuario = binding.InicioSesionEtUsuario.editText?.text.toString()
        val contrasena = binding.InicioSesionEtContrasena.editText?.text.toString()

        if (correoUsuario.isEmpty() || contrasena.isEmpty()) {
            showErrorMessageDialog("Por favor, ingresa un correo electrónico y una contraseña válidos.")
            return
        }

        auth.signInWithEmailAndPassword(correoUsuario, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("InicioSesion", "Logueado el usuario $correoUsuario ")
                    pasarAlMenu()
                } else {
                    handleAuthError(task.exception)
                }
            }
    }

    private fun pasarAlMenu() {
        println(auth.currentUser?.uid)
        if (auth.currentUser?.uid == null) {
            return
        }

        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun handleAuthError(exception: Exception?) {
        if (exception is FirebaseAuthInvalidCredentialsException || exception is FirebaseAuthInvalidUserException) {
            showErrorMessageDialog("Has ingresado un correo o contraseña inválidos")
        } else {
            showErrorMessageDialog("Ha ocurrido un error. Por favor, intenta nuevamente.")
        }
    }

    private fun showErrorMessageDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error de autenticación")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}