package com.tfg.smartdiet.splash

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.tfg.smartdiet.R
import android.content.Intent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.tfg.smartdiet.databinding.ActivityBienvenidaBinding
import com.tfg.smartdiet.databinding.SplashScreenBinding
import com.tfg.smartdiet.iu.Bienvenida.BienvenidaActivity

class Splash:AppCompatActivity(){
    private lateinit var binding: SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding =
            SplashScreenBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)*/
        setContentView(R.layout.splash_screen)
        installSplashScreen()
        Thread.sleep(3000)
        val i = Intent(this, BienvenidaActivity::class.java)
        startActivity(i)
    }
}