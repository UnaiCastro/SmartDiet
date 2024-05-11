package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import EntryAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tfg.smartdiet.R


import com.tfg.smartdiet.databinding.FragmentPrincipalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class DietaFragment : Fragment(), EntryAdapter.OnItemLongClickListener {



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

    private lateinit var entryAdapter: EntryAdapter
    private lateinit var recyclerViewEntries: RecyclerView

    private val allEntries: MutableList<HashMap<String, Any>> = mutableListOf()
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

        initRecyclerView()
        addTestEntries() // Test con entradas existentes (cambiar por lectura de db)
        initUI()
        initListener()
        checkReset()
    }

    private fun initRecyclerView() {
        recyclerViewEntries = binding.recyclerViewEntries
        recyclerViewEntries.layoutManager = LinearLayoutManager(requireContext())
        entryAdapter = EntryAdapter(allEntries, this) // Pass the listener to the adapter
        recyclerViewEntries.adapter = entryAdapter
    }

    private fun addTestEntries() {
        allEntries.addAll(
            listOf(
                hashMapOf(
                    "nombre" to "Desayuno",
                    "calorias" to 100,
                    "proteinas" to 20,
                    "grasas" to 10,
                    "carbohidratos" to 30,
                    "hora" to "10:10"
                ),
                hashMapOf(
                    "nombre" to "Merienda",
                    "calorias" to 150,
                    "proteinas" to 25,
                    "grasas" to 15,
                    "carbohidratos" to 35,
                    "hora" to "17:10"
                )
                // Add more test entries as needed
            )
        )

        entryAdapter.notifyDataSetChanged()
    }
    private fun addEntry(entryData: HashMap<String, Any>) {

        // Add the entry data to the list
        allEntries.add(entryData)

        // Notify the adapter of the data change
        entryAdapter.notifyDataSetChanged()

        // TODO: Añadir en la base de datos etc...
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

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.nuevoRegistro))
            .setPositiveButton(getString(R.string.annadir)) { _, _ ->
                val newNombre =dialogView.findViewById<EditText>(R.id.etNombre).text.toString()
                val newCalorias = dialogView.findViewById<EditText>(R.id.etNewCalorias).text.toString().toInt()
                val newProteinas = dialogView.findViewById<EditText>(R.id.etNewProteinas).text.toString().toInt()
                val newGrasas = dialogView.findViewById<EditText>(R.id.etNewGrasas).text.toString().toInt()
                val newCarbohidratos = dialogView.findViewById<EditText>(R.id.etNewCarbohidratos).text.toString().toInt()

                // Store the new entry data in a HashMap
                val newEntryData: HashMap<String, Any> = hashMapOf(
                    "nombre" to newNombre,
                    "calorias" to newCalorias,
                    "proteinas" to newProteinas,
                    "grasas" to newGrasas,
                    "carbohidratos" to newCarbohidratos
                )
                val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                newEntryData["hora"] = hora

                // Add the new entry
                addEntry(newEntryData)


                // Actualizar las variables con los nuevos valores
                calorias += newCalorias
                proteinas += newProteinas
                grasas += newGrasas
                carbohidratos += newCarbohidratos

                // Actualizar las barras de progreso
                initUI()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .create()

        dialog.show()
    }

    override fun onItemLongClick(position: Int) {
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val context = requireContext()
        MaterialAlertDialogBuilder(context)
            .setMessage(getString(R.string.deleteConf))
            .setPositiveButton(getString(R.string.y)) { _, _ ->
                deleteEntry(position)
            }
            .setNegativeButton(getString(R.string.n), null)
            .show()
    }

    private fun deleteEntry(position: Int) {
        allEntries.removeAt(position)
        entryAdapter.notifyDataSetChanged()
        // TODO: Borrar de la base de datos etc...
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