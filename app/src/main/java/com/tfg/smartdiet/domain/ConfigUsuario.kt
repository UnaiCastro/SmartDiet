package com.tfg.smartdiet.domain

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
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
    fun setTema(tema: String){
        val editor = prefs.edit()
        editor.putString("TEMA",tema)
        editor.apply()
        if(tema == "NORMAL"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun initTema(): String? {
        val tema = prefs.getString("TEMA","NORMAL")
        if(tema == "NORMAL"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        return tema
    }

}

