package com.tfg.smartdiet.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.MainActivity
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

    fun setIdioma(idioma: String, context: Context) {
        val locale = Locale(idioma)
        val editor = prefs.edit();
        editor.putString("PREF_IDIOMA", idioma);
        editor.apply();

        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        Log.d("TAG", "Se ha cambiado el idioma a $idioma")
    }

    fun initIdioma(context:Context): String{
        val idioma = prefs.getString("PREF_IDIOMA","es")!!
/*        val locale = Locale(idioma)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)*/
        Log.d("TAG", "El idioma de la aplicación es $idioma")
        setIdioma(idioma,context)

        return idioma
    }

    fun getNotis(): String {
        return prefs.getString("NOTIFICACION","ON")!!
    }

    fun setNotis(noti: String){
        val editor = prefs.edit();
        editor.putString("NOTIFICACION", noti);
        editor.apply();
    }
}

