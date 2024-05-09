package com.tfg.smartdiet.domain

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//Método usado para las preferencias del usuario
class ConfigUsuario(private val prefs: SharedPreferences)  {
    val sesionAbierta: Boolean

        //Devuelve si hay una sesión o no
        get() =//SharedPreferences.Editor editor = prefs.edit();

            prefs.getBoolean("SESION", false)

    fun setInicioSesion(mail: String) {

        val editor = prefs.edit()
        editor.putBoolean("SESION",true)
        editor.putString("USUARIO", mail)
        editor.apply()
    }
    val getMail: String
        get() = prefs.getString("USUARIO","") ?: ""

}

