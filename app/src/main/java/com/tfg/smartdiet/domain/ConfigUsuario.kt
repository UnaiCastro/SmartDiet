package com.tfg.smartdiet.domain

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

//Método usado para las preferencias del usuario
class ConfigUsuario(private val prefs: SharedPreferences)  {

    fun sesionAbierta():Boolean{
        val sesion=prefs.getBoolean("SESION", false)

        return sesion
    }

    fun setInicioSesion(mail: String) {

        val editor = prefs.edit()
        editor.putBoolean("SESION",true)
        editor.apply()
    }

    fun cerrarSesion(){
        val editor = prefs.edit()
        editor.putBoolean("SESION",false)
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

    fun setIdioma(idioma: String, context: Context){
        val editor = prefs.edit();
        editor.putString("PREF_IDIOMA", idioma);
        editor.apply();
        val locale = Locale(idioma)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    fun initIdioma(context:Context): String{
        val idioma = prefs.getString("PREF_IDIOMA","es")!!
        val locale = Locale(idioma)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        setIdioma(idioma,context)
        return idioma
    }

}

