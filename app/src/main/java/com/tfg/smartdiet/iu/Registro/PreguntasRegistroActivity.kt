package com.tfg.smartdiet.iu.Registro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityInicioSesionBinding
import com.tfg.smartdiet.databinding.ActivityPreguntasRegistroBinding
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import java.text.DecimalFormat

class PreguntasRegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreguntasRegistroBinding
    private var peso:Int=120
    private var sexo:String="Hombre"
    private var objetivo:String="Adelgazar"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityPreguntasRegistroBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.PREntrar.setOnClickListener {
            val data = Intent().apply {
                putExtra("peso", peso)
                putExtra("sexo", sexo)
                putExtra("objetivo", objetivo)
            }
            setResult(RESULT_OK, data)
            finish()
        }
        binding.rsWidth.addOnChangeListener { _, value, _ ->
            val df = DecimalFormat("#.##")
            peso = df.format(value).toInt()
            binding.tvWidth.text = "$peso kg"
        }
        binding.HombreB.setOnClickListener {
            sexo = binding.HombreB.text.toString()
        }
        binding.MujerB.setOnClickListener {
            sexo = binding.MujerB.text.toString()
        }
        binding.PerderGRasaB.setOnClickListener {
            objetivo = binding.PerderGRasaB.tag.toString()
        }
        binding.AumentarVolumenB.setOnClickListener {
            objetivo = binding.AumentarVolumenB.tag.toString()
        }
        binding.EstarFormaB.setOnClickListener {
            objetivo = binding.EstarFormaB.tag.toString()
        }
    }

}