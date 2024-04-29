package com.tfg.smartdiet.iu.InicioSesion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tfg.smartdiet.databinding.ActivityInicioSesionBinding

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityInicioSesionBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.InicioSesionBtnEntrar.setOnClickListener {

        }
    }
}