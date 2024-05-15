package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import EntryAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R


import com.tfg.smartdiet.databinding.FragmentPrincipalBinding
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.domain.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class DietaFragment : Fragment(), EntryAdapter.OnItemLongClickListener {



    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!

    private var calorias = 0
    private var proteinas = 0
    private var grasas = 0
    private var carbohidratos = 0

    private var MAX_CALORIAS_DIARIAS=100
    private var MAX_PROTEINAS_DIARIAS=100
    private var MAX_GRASAS_DIARIAS=100
    private var MAX_CARBOHIDRATOS_DIARIOS=100

    private lateinit var entryAdapter: EntryAdapter
    private lateinit var recyclerViewEntries: RecyclerView

    private val allEntries: MutableList<HashMap<String, Any>> = mutableListOf()

    private val entryIds: MutableList<String> = mutableListOf()


    private lateinit var db:FirebaseFirestore

    private var dietaActID: String? = null

    private lateinit var auth: FirebaseAuth
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

        obtenerDietaActual { dietaActID ->
            this.dietaActID = dietaActID
            dietaActID?.let {
                setDietaObjectives(it)
                setCurrentValues(it)
                fetchEntries(it)}
            initRecyclerView()
            initUI()
            initListener()
            checkReset()
            Log.i("DietaFragment_DB", "ID dieta actual: ${this.dietaActID}")
        }
//        crearDietaUsuario() // SOLO USAR UNA VEZ PARA AÑADIR DIETA DE PRUEBA
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

        // Añadir en la base de datos
        annadirEntradaEnFirestore(entryData)


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

    private fun calcularProgreso(valor: Int, maximo: Int): Int {
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

                // Añadir la dieta actual
                newEntryData["dietaActID"] = dietaActID ?: "" // Sin esto pone type mismatch

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
        val entryToDelete = allEntries[position]
        // Retrieve the entry ID from entryIds based on the position
        val entryIdToDelete = entryIds[position]

        // Log the entryToDelete to verify its contents
        Log.d("DietaFragment_DB", "Entry to delete: $entryToDelete, ID: $entryIdToDelete")

        db = FirebaseFirestore.getInstance()



        // Delete the entry from Firestore using the retrieved entry ID
        db.collection("entries")
            .document(entryIdToDelete)
            .delete()
            .addOnSuccessListener {
                Log.i("DietaFragment_DB", "Entry deleted successfully")
                // Remove the entry ID from entryIds array
                entryIds.removeAt(position)
                // Remove the entry from the list and notify adapter
                allEntries.removeAt(position)
                // Recalcular totales y actualizar
                updateAndRetrieveTotalValues()
                entryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error deleting entry: ${e.message}")
            }


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

    private fun annadirEntradaEnFirestore(entrada: HashMap<String, Any>) {
        db = FirebaseFirestore.getInstance()

        // Convert the values in the entrada HashMap to strings
        val stringEntry: HashMap<String, String> = entrada.mapValues { it.value.toString() } as HashMap<String, String>

        db.collection("entries")
            .add(stringEntry) // Upload the string entry to Firestore
            .addOnSuccessListener { documentReference ->
                val entryId = documentReference.id
                entryIds.add(entryId)
                Log.i("DietaFragment_DB", "Añadida entrada $entrada con ID: $entryId")
                // Recalcular totales y actualizar
                updateAndRetrieveTotalValues()
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "La entrada $entrada no se pudo añadir ${e.message}")
            }
    }


    private fun obtenerDietaActual(callback: (String?) -> Unit) {
        // Obtener el correo del usuario desde firebase

        auth = FirebaseAuth.getInstance()
        val correo = auth.currentUser?.email


        db = FirebaseFirestore.getInstance()

        // Obtener el id de la dieta actual del usuario a partir de su correo
        db.collection("users")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val dietaActID = documents.documents[0].getString("dietaActID")
                    Log.i("DietaFragment_DB", "Dieta actual: $dietaActID")
                    callback(dietaActID) // Return the dietaActID through the callback
                } else {
                    Log.e("DietaFragment_DB", "No se encontró ningún usuario con el correo proporcionado: $correo")
                    callback(null) // Return null through the callback if no user is found
                }
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error al buscar usuario: ${e.message}")
                callback(null) // Return null through the callback in case of failure
            }
    }


    private fun crearDietaUsuario(){
        // Obtener el correo del usuario desde firebase

        auth = FirebaseAuth.getInstance()
        val correo = auth.currentUser?.email

        // Obtener el id del usuario a partir de su correo
        db.collection("users")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userID = documents.documents[0].id

                    // Añadir dieta
                    val newDieta: HashMap<String, Any> = hashMapOf(
                        "userID" to userID,
                        "caloriasObj" to "2000",
                        "proteinasObj" to "100",
                        "grasasObj" to "50",
                        "carbohidratosObj" to "100",
                        "caloriasAct" to "200",
                        "proteinasAct" to "20",
                        "grasasAct" to "30",
                        "carbohidratosAct" to "40",
                        "fecha" to "12-05-2024"
                    )

                    db.collection("dietas")
                        .add(newDieta)
                        .addOnSuccessListener { dietaDocRef ->
                            Log.i("DietaFragment_DB", "Añadida la dieta $newDieta")

                            // Update the user document with the dietaActID
                            db.collection("users")
                                .document(userID)
                                .update("dietaActID", dietaDocRef.id)
                                .addOnSuccessListener {
                                    Log.i("DietaFragment_DB", "Actualizado dietaActID en el usuario: $userID")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("DietaFragment_DB", "Error al actualizar dietaActID en el usuario: $userID, ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DietaFragment_DB", "La dieta $newDieta no se pudo añadir ${e.message}")
                        }

                } else {
                    Log.e("DietaFragment_DB", "No se encontró ningún usuario con el correo proporcionado: $correo")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error al buscar usuario: ${e.message}")
            }
    }

    private fun setDietaObjectives(dietaID: String) {
        db.collection("dietas")
            .document(dietaID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val caloriasObj = document.getString("caloriasObj")?.toIntOrNull() ?: 0
                    val proteinasObj = document.getString("proteinasObj")?.toIntOrNull() ?: 0
                    val grasasObj = document.getString("grasasObj")?.toIntOrNull() ?: 0
                    val carbohidratosObj = document.getString("carbohidratosObj")?.toIntOrNull() ?: 0

                    // Set the attributes with the retrieved objectives
                    MAX_CALORIAS_DIARIAS = caloriasObj
                    MAX_PROTEINAS_DIARIAS = proteinasObj
                    MAX_GRASAS_DIARIAS = grasasObj
                    MAX_CARBOHIDRATOS_DIARIOS = carbohidratosObj
                    initUI()
                } else {
                    Log.e("DietaFragment_DB", "No existe la dieta con ID: $dietaID")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error al obtener la dieta con ID $dietaID: ${e.message}")
            }
    }


    private fun setCurrentValues(dietaID: String) {
        db.collection("dietas")
            .document(dietaID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val caloriasAct = document.getString("caloriasAct")?.toIntOrNull() ?: 0
                    val proteinasAct = document.getString("proteinasAct")?.toIntOrNull() ?: 0
                    val grasasAct = document.getString("grasasAct")?.toIntOrNull() ?: 0
                    val carbohidratosAct = document.getString("carbohidratosAct")?.toIntOrNull() ?: 0

                    // Set the variables with the retrieved current values
                    calorias = caloriasAct
                    proteinas = proteinasAct
                    grasas = grasasAct
                    carbohidratos = carbohidratosAct
                    initUI()
                } else {
                    Log.e("DietaFragment_DB", "No existe la dieta con ID: $dietaID")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error al obtener la dieta con ID $dietaID: ${e.message}")
            }
    }

    private fun fetchEntries(dietaID: String) {
        db.collection("entries")
            .whereEqualTo("dietaActID", dietaID)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val entryData = document.data
                    val entryId = document.id

                    // Retrieve the values as strings and then convert them to Int
                    val calorias = entryData["calorias"]?.toString()?.toIntOrNull() ?: 0
                    val proteinas = entryData["proteinas"]?.toString()?.toIntOrNull() ?: 0
                    val grasas = entryData["grasas"]?.toString()?.toIntOrNull() ?: 0
                    val carbohidratos = entryData["carbohidratos"]?.toString()?.toIntOrNull() ?: 0

                    // Add entry data to allEntries list
                    val entryMap: HashMap<String, Any> = hashMapOf(
                        "nombre" to entryData["nombre"].toString(),
                        "calorias" to calorias,
                        "proteinas" to proteinas,
                        "grasas" to grasas,
                        "carbohidratos" to carbohidratos,
                        "hora" to entryData["hora"].toString()
                    )
                    allEntries.add(entryMap)

                    // Add entry ID to entryIds list
                    entryIds.add(entryId)
                }
                updateAndRetrieveTotalValues()

                // Notify adapter of data change after all entries are fetched
                entryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error al obtener las entradas de la dieta con ID $dietaID: ${e.message}")
            }
    }



    private fun updateAndRetrieveTotalValues() {
        // Log all the entries
        Log.i("DietaFragment_DB", "All Entries: $allEntries")

        var totalCalorias = 0
        var totalProteinas = 0
        var totalGrasas = 0
        var totalCarbohidratos = 0

        // Iterate through each entry in allEntries
        for (entryData in allEntries) {
            // Log the types of values
            Log.d("DietaFragment_DB", "Type of calorias: ${entryData["calorias"]?.javaClass?.simpleName}")
            Log.d("DietaFragment_DB", "Type of proteinas: ${entryData["proteinas"]?.javaClass?.simpleName}")
            Log.d("DietaFragment_DB", "Type of grasas: ${entryData["grasas"]?.javaClass?.simpleName}")
            Log.d("DietaFragment_DB", "Type of carbohidratos: ${entryData["carbohidratos"]?.javaClass?.simpleName}")

            // Sum up the values for each field
            totalCalorias += (entryData["calorias"] as? Int) ?: 0
            totalProteinas += (entryData["proteinas"] as? Int) ?: 0
            totalGrasas += (entryData["grasas"] as? Int) ?: 0
            totalCarbohidratos += (entryData["carbohidratos"] as? Int) ?: 0
        }


        // Log the calculated total values
        Log.i("DietaFragment_DB", "Total Calorias: $totalCalorias")
        Log.i("DietaFragment_DB", "Total Proteinas: $totalProteinas")
        Log.i("DietaFragment_DB", "Total Grasas: $totalGrasas")
        Log.i("DietaFragment_DB", "Total Carbohidratos: $totalCarbohidratos")

        // Update the total values in the database
        val dietaID = dietaActID ?: return // Exit if dietaActID is null

        db.collection("dietas")
            .document(dietaID)
            .update(
                mapOf(
                    "caloriasAct" to totalCalorias.toString(),
                    "proteinasAct" to totalProteinas.toString(),
                    "grasasAct" to totalGrasas.toString(),
                    "carbohidratosAct" to totalCarbohidratos.toString()
                )
            )
            .addOnSuccessListener {
                Log.i("DietaFragment_DB", "Total values updated successfully in the database")
                // Once values are updated, retrieve them again from the database
                setCurrentValues(dietaID)
            }
            .addOnFailureListener { e ->
                Log.e("DietaFragment_DB", "Error updating total values in the database: ${e.message}")
            }
    }





}