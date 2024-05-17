package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentGestionDietasBinding
import com.tfg.smartdiet.domain.Dieta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GestionDietasFragment : Fragment() {

    private var _binding: FragmentGestionDietasBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    private val logtag = "GestionDietasFragment"

    private var currentCalorias: String = ""
    private var currentProteinas: String = ""
    private var currentGrasas: String = ""
    private var currentCarbohidratos: String = ""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGestionDietasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        setupUI()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        // Display current objectives
        retrieveAndSetCurrentDietValues()

        // Add onClickListener to the edit buttons
        binding.btnEditCalorias.setOnClickListener {
            toggleEditMode(binding.etCaloriasObj)
        }

        binding.btnEditProteinas.setOnClickListener {
            toggleEditMode(binding.etProteinasObj)
        }

        binding.btnEditGrasas.setOnClickListener {
            toggleEditMode(binding.etGrasasObj)
        }

        binding.btnEditCarbohidratos.setOnClickListener {
            toggleEditMode(binding.etCarbohidratosObj)
        }

        // Add onFocusChangeListener to all EditText fields
        binding.etCaloriasObj.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                disableEditMode(binding.etCaloriasObj)
            }
        }

        binding.etProteinasObj.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                disableEditMode(binding.etProteinasObj)
            }
        }

        binding.etGrasasObj.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                disableEditMode(binding.etGrasasObj)
            }
        }

        binding.etCarbohidratosObj.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                disableEditMode(binding.etCarbohidratosObj)
            }
        }

        // Add text change listeners to EditText fields
        binding.etCaloriasObj.addTextChangedListener {
            updateEditTextStyle(binding.etCaloriasObj, it.toString(), currentCalorias)
        }

        binding.etProteinasObj.addTextChangedListener {
            updateEditTextStyle(binding.etProteinasObj, it.toString(), currentProteinas)
        }

        binding.etGrasasObj.addTextChangedListener {
            updateEditTextStyle(binding.etGrasasObj, it.toString(), currentGrasas)
        }

        binding.etCarbohidratosObj.addTextChangedListener {
            updateEditTextStyle(binding.etCarbohidratosObj, it.toString(), currentCarbohidratos)
        }

        // Add onClickListener to the save button
        binding.btnGuardar.setOnClickListener {

            saveDietDataToFirestore()

            // Disable editing for all EditText fields after saving
            disableEditing()
        }
    }

    private fun updateEditTextStyle(editText: EditText, newText: String, currentValue: String) {
        if (newText != currentValue) {
            editText.setTypeface(null, Typeface.BOLD)
        } else {
            editText.setTypeface(null, Typeface.NORMAL)
        }
    }


    private fun toggleEditMode(editText: EditText) {
        editText.isEnabled = !editText.isEnabled
        if (editText.isEnabled) {
            editText.requestFocus()
        }
    }

    private fun disableEditMode(editText: EditText) {
        editText.isEnabled = false
    }

    private fun disableEditing() {
        // Disable editing for all EditText fields
        binding.etCaloriasObj.isEnabled = false
        binding.etProteinasObj.isEnabled = false
        binding.etGrasasObj.isEnabled = false
        binding.etCarbohidratosObj.isEnabled = false
    }

    private fun saveDietDataToFirestore() {
        // Retrieve the user-entered data from EditText fields
        val caloriasObj = binding.etCaloriasObj.text.toString()
        val proteinasObj = binding.etProteinasObj.text.toString()
        val grasasObj = binding.etGrasasObj.text.toString()
        val carbohidratosObj = binding.etCarbohidratosObj.text.toString()

        // Retrieve the current user's ID
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        // Ensure that current user ID is not null
        currentUserID?.let { userID ->
            // Get the user document reference
            val userDocRef = db.collection("users").document(userID)

            // Retrieve the dietaActID from the user document
            userDocRef.get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    val dietaActID = userDocumentSnapshot.getString("dietaActID")

                    // Ensure that dietaActID is not null
                    dietaActID?.let { dietaID ->
                        // Update the existing dieta document with the new values
                        val dietaDocRef = db.collection("dietas").document(dietaID)
                        val updatedData = hashMapOf<String, Any>(
                            "caloriasObj" to caloriasObj,
                            "carbohidratosObj" to carbohidratosObj,
                            "grasasObj" to grasasObj,
                            "proteinasObj" to proteinasObj
                        )

                        // Explicitly cast the HashMap to MutableMap<String, Any>
                        val updatedDataMap: MutableMap<String, Any> = updatedData

                        dietaDocRef.update(updatedDataMap)
                            .addOnSuccessListener {
                                Log.i(logtag, "Se ha actualizado la dieta en Firestore")

                                // Disable editing for all EditText fields after saving
                                disableEditing()

                                // Show a toast message indicating successful save
                                Toast.makeText(requireContext(),getString(R.string.toastDietaActualizada) , Toast.LENGTH_SHORT).show()

                                // Navigate back to the previous fragment
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Log.e(logtag, "Error al actualizar la dieta en Firestore: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(logtag, "Error al obtener el documento del usuario: ${e.message}")
                }
        }
    }


    private fun retrieveAndSetCurrentDietValues() {
        // Retrieve the current user's ID
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        // Ensure that current user ID is not null
        currentUserID?.let { userID ->
            // Get the user document reference
            val userDocRef = db.collection("users").document(userID)

            // Retrieve the dietaActID from the user document
            userDocRef.get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    val dietaActID = userDocumentSnapshot.getString("dietaActID")
                    Log.d(logtag, "dietaActID: $dietaActID")

                    // Ensure that dietaActID is not null
                    dietaActID?.let { dietaID ->
                        // Get the dieta document reference
                        val dietaDocRef = db.collection("dietas").document(dietaID)

                        // Retrieve the dieta data from Firestore
                        dietaDocRef.get()
                            .addOnSuccessListener { dietaDocumentSnapshot ->
                                currentCalorias = dietaDocumentSnapshot.getString("caloriasObj") ?: ""
                                currentProteinas = dietaDocumentSnapshot.getString("proteinasObj") ?: ""
                                currentGrasas = dietaDocumentSnapshot.getString("grasasObj") ?: ""
                                currentCarbohidratos = dietaDocumentSnapshot.getString("carbohidratosObj") ?: ""

                                Log.d(logtag, "currentCalorias: $currentCalorias")
                                Log.d(logtag, "currentProteinas: $currentProteinas")
                                Log.d(logtag, "currentGrasas: $currentGrasas")
                                Log.d(logtag, "currentCarbohidratos: $currentCarbohidratos")

                                // Set the retrieved values in the EditText fields
                                binding.etCaloriasObj.setText(currentCalorias)
                                binding.etProteinasObj.setText(currentProteinas)
                                binding.etGrasasObj.setText(currentGrasas)
                                binding.etCarbohidratosObj.setText(currentCarbohidratos)
                            }
                            .addOnFailureListener { e ->
                                Log.e(logtag, "Error retrieving dieta document: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(logtag, "Error retrieving user document: ${e.message}")
                }
        }
    }






}
