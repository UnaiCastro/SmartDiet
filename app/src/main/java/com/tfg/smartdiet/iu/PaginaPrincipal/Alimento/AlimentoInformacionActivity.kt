package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityAlimentoInformacionBinding
import com.tfg.smartdiet.databinding.ActivityHistoricoBinding

class AlimentoInformacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlimentoInformacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlimentoInformacionBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        val nombre = intent.getStringExtra("nombre").toString()
        val agua = intent.getStringExtra("agua").toString()
        val calorias = intent.getStringExtra("calorias").toString()
        val carbohidratos = intent.getStringExtra("carbohidratos").toString()
        val descripcion = intent.getStringExtra("descripcion").toString()
        val grasas = intent.getStringExtra("grasas").toString()
        val minerales = intent.getStringExtra("minerales").toString()
        val proteinas = intent.getStringExtra("proteinas").toString()
        val imagen = intent.getStringExtra("imagen").toString()
        val tipo = intent.getStringExtra("tipo").toString()
        val vitaminas = intent.getStringExtra("vitaminas").toString()

        Glide.with(this).load(imagen).into(binding.AlimentoInforFoto)
        binding.AlimentoInforAgua.text=getString(R.string.agua) + ": $agua"
        binding.AlimentoInforCalorias.text=getString(R.string.calorias) + ": $calorias"
        binding.AlimentoInforGrasas.text=getString(R.string.grasas) + ": $grasas"
        binding.AlimentoInforCarbohidratos.text=getString(R.string.carbs) + ": $carbohidratos"
        binding.AlimentoInforDescripcion.text="$descripcion"
        binding.AlimentoInforMinerales.text=getString(R.string.minerales) + ": $minerales"
        binding.AlimentoInforNombre.text="$nombre"
        binding.AlimentoInforProteinas.text=getString(R.string.proteinas) + ": $proteinas"
        binding.AlimentoInforVitaminas.text=getString(R.string.vitaminas) + ": $vitaminas"
        binding.AlimentoInforTipo.text=getString(R.string.tipo) + ": $tipo"




    }
}