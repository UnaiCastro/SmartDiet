package com.tfg.smartdiet.iu.PaginaPrincipal.PrimeraPantalla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentPrincipalBinding

class PrincipalFragment : Fragment() {


    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private var calorias=15
    private var MAX_CALORIAS_DIARIAS=100.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPrincipalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.BotonB.setOnClickListener {
            calorias += 45
            initUI()
        }
    }

    private fun initUI() {
        // Obtén una referencia al ProgressBar
        val progressBar: ProgressBar = binding.progressBar

        // Supongamos que tienes una variable que almacena las calorías consumidas
        val caloriasConsumidas: Int = obtenerCaloriasConsumidas()

        // Calcula el progreso como un porcentaje
        val progreso: Int = (caloriasConsumidas / MAX_CALORIAS_DIARIAS.toFloat() * 100).toInt()

        // Actualiza el progreso del ProgressBar
        progressBar.progress = progreso
    }

    private fun obtenerCaloriasConsumidas(): Int {
        return this.calorias
    }


}