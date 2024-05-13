package com.tfg.smartdiet.domain

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

//Método usado para las preferencias del usuario
class ConfigUsuario(private val prefs: SharedPreferences)  {
    private lateinit var auth:FirebaseAuth

    fun sesionAbierta():Boolean{
        val sesion=prefs.getBoolean("SESION", false)

        return sesion
    }

    fun setInicioSesion(mail: String) {

        val editor = prefs.edit()
        editor.putBoolean("SESION",true)
        editor.apply()
    }
    val getMail: String
        get() = prefs.getString("USUARIO","") ?: ""

}

