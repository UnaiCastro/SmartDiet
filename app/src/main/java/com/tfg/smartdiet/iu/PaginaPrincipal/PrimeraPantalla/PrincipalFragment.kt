package com.tfg.smartdiet.iu.PaginaPrincipal.PrimeraPantalla

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentPrincipalBinding


class PrincipalFragment : Fragment() {


    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!

    private var calorias=15
    private var proteinas = 0
    private var grasas = 0
    private var carbohidratos = 0

    private var MAX_CALORIAS_DIARIAS=300.0f
    private var MAX_PROTEINAS_DIARIAS=100.0f
    private var MAX_GRASAS_DIARIAS=100.0f
    private var MAX_CARBOHIDRATOS_DIARIOS=100.0f

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
        checkReset()
    }

    private fun initListener() {
        binding.BotonB.setOnClickListener {
            showAddDataDialog()
        }
    }

    private fun initUI() {
        // Actualiza la barra de progreso para las calorías
        binding.progressBarCalorias.progress = calcularProgreso(calorias, MAX_CALORIAS_DIARIAS)
        binding.textViewCalorias.text = "$calorias/$MAX_CALORIAS_DIARIAS"

        // Actualiza la barra de progreso para las proteínas
        binding.progressBarProteinas.progress = calcularProgreso(proteinas, MAX_PROTEINAS_DIARIAS)
        binding.textViewProteinas.text = "$proteinas/$MAX_PROTEINAS_DIARIAS"

        // Actualiza la barra de progreso para las grasas
        binding.progressBarGrasas.progress = calcularProgreso(grasas, MAX_GRASAS_DIARIAS)
        binding.textViewGrasas.text = "$grasas/$MAX_GRASAS_DIARIAS"

        // Actualiza la barra de progreso para los carbohidratos
        binding.progressBarCarbohidratos.progress = calcularProgreso(carbohidratos, MAX_CARBOHIDRATOS_DIARIOS)
        binding.textViewCarbohidratos.text = "$carbohidratos/$MAX_CARBOHIDRATOS_DIARIOS"
    }

    private fun calcularProgreso(valor: Int, maximo: Float): Int {
        return ((valor.toFloat() / maximo) * 100).toInt()
    }

    private fun showAddDataDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_data, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.AppTheme)
            .setView(dialogView)
            .setTitle("Añadir Datos")
            .setPositiveButton("Añadir") { _, _ ->
                val newCalorias = dialogView.findViewById<EditText>(R.id.etNewCalorias).text.toString().toInt()
                val newProteinas = dialogView.findViewById<EditText>(R.id.etNewProteinas).text.toString().toInt()
                val newGrasas = dialogView.findViewById<EditText>(R.id.etNewGrasas).text.toString().toInt()
                val newCarbohidratos = dialogView.findViewById<EditText>(R.id.etNewCarbohidratos).text.toString().toInt()

                // Actualizar las variables con los nuevos valores
                calorias += newCalorias
                proteinas += newProteinas
                grasas += newGrasas
                carbohidratos += newCarbohidratos

                // Actualizar las barras de progreso
                initUI()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun checkReset(){
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val resetRequired = sharedPreferences.getBoolean("reset", false)

        if (resetRequired) {
            //TODO: Guardar los valores actuales en la base de datos

            calorias = 0
            proteinas = 0
            grasas = 0
            carbohidratos = 0

            val editor = sharedPreferences.edit()
            editor.putBoolean("reset", false)
            editor.apply()
        }
    }

}