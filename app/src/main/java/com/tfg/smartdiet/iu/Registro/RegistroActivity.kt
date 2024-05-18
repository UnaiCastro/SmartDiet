package com.tfg.smartdiet.iu.Registro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityRegistroBinding
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.domain.Usuario
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private val PREGUNTAS_REGISTRO_REQUEST_CODE = 1


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

        binding.InicioSesionTvTextoSubrayado.setOnClickListener {
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registrarUsuario() {
        val etNombre = binding.REgistroEtUsuario.editText?.text.toString()
        val etEmail = binding.RegistroEtEmail.editText?.text.toString()
        val etContrasena = binding.RegistroEtContrasena.editText?.text.toString()

        binding.RegistroEtEmail.error = null
        binding.RegistroEtContrasena.error = null

        var registroOK = true

        if (!isValidEmail(etEmail)) {
            binding.RegistroEtEmail.error = getString(R.string.formatoEmail)
            registroOK = false
        }

        if (etContrasena.length < 6) {
            binding.RegistroEtContrasena.error = getString(R.string.passwordLength)
            registroOK = false
        }

        if (!registroOK) {
            return
        }

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(etEmail, etContrasena).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Se guarda la sesión
                val conf = ConfigUsuario(getSharedPreferences("Configuracion", Context.MODE_PRIVATE))
                conf.setInicioSesion(etNombre)
                val i = Intent(this, PreguntasRegistroActivity::class.java)
                startActivityForResult(i, PREGUNTAS_REGISTRO_REQUEST_CODE)

            } else {
                handleAuthError(task.exception)
                Log.i("Registro", "${task.result}")
            }

        }.addOnFailureListener { exception ->
            handleAuthError(exception)
            Log.e("Registro", "${exception.message}")
        }

    }

    private fun handleAuthError(exception: Exception?) {
        if (exception is FirebaseAuthInvalidCredentialsException ||
            exception is FirebaseAuthInvalidUserException ||
            exception is FirebaseAuthUserCollisionException
        ) {
            // Handle specific error types
            showErrorMessageDialog(getString(R.string.hasingresadoinvalidos))
        } else {
            // Handle other errors
            showErrorMessageDialog(getString(R.string.errorIntentaNuevamente))
        }
    }


    private fun showErrorMessageDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.errorRegistro))
            .setMessage(message)
            .setPositiveButton(getString(R.string.aceptar), null)
            .show()
    }

    private fun registrarUsuarioEnFirestore(
        etNombre: String,
        etEmail: String,
        etGenero: String,
        etObjetivo: String,
        etPeso: String
    ) {

        db= FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val usuarioID = auth.currentUser?.uid
        val user = Usuario (
            userID = usuarioID.toString(),
            nombreUsuario = etNombre,
            correo = etEmail,
            genero = etGenero,
            objetivo = etObjetivo,
            peso = etPeso
        ).toMap()
        usuarioID?.let { db.collection("users").document(it).set(user) }?.addOnSuccessListener {
            Log.i("Registro","Creado usuario $usuarioID")
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish()
        }?.addOnFailureListener {
            Log.e("Registro","Creacion de Usuario $usuarioID fallida ${it.message}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PREGUNTAS_REGISTRO_REQUEST_CODE && resultCode == RESULT_OK) {
            val etNombre = binding.REgistroEtUsuario.editText?.text.toString()
            val etEmail = binding.RegistroEtEmail.editText?.text.toString()
            val etGenero = data?.getStringExtra("sexo") ?: ""
            val etObjetivo = data?.getStringExtra("objetivo") ?: ""
            val etPeso = data?.getIntExtra("peso", 0).toString()

            registrarUsuarioEnFirestore(etNombre, etEmail, etGenero, etObjetivo, etPeso)
        }
    }

}