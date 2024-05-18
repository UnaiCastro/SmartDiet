package com.tfg.smartdiet.iu.InicioSesion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityInicioSesionBinding
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.iu.PaginaPrincipal.Historico.HistoricoActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity
import com.tfg.smartdiet.iu.Registro.RegistroActivity

class InicioSesionActivity : AppCompatActivity() {
    companion object {
        fun logOut(context:Activity) {
            val intent = Intent(context, InicioSesionActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            } // navegamos a inicio
            context.startActivity(intent)
            context.finish()
        }
    }

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
            showErrorMessageDialog(getString(R.string.ingresaCorreoYContraValidos))
            return
        }

        auth.signInWithEmailAndPassword(correoUsuario, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("InicioSesion", "Logueado el usuario $correoUsuario ")
                    pasarAlMenu(correoUsuario)
                } else {
                    handleAuthError(task.exception)
                }
            }
    }

    private fun pasarAlMenu(correoUsuario: String) {
        println(auth.currentUser?.uid)
        if (auth.currentUser?.uid == null) {
            return
        }
        val conf = ConfigUsuario(getSharedPreferences("Configuracion", Context.MODE_PRIVATE))
        conf.setInicioSesion(correoUsuario)
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun handleAuthError(exception: Exception?) {
        if (exception is FirebaseAuthInvalidCredentialsException || exception is FirebaseAuthInvalidUserException) {
            showErrorMessageDialog(getString(R.string.hasingresadoinvalidos))
        } else {
            showErrorMessageDialog(getString(R.string.errorIntentaNuevamente))
        }
    }

    private fun showErrorMessageDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.errorAutenticacion))
            .setMessage(message)
            .setPositiveButton(getString(R.string.aceptar), null)
            .show()
    }
}