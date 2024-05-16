package com.tfg.smartdiet.iu.PaginaPrincipal

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityMainBinding
import com.tfg.smartdiet.domain.Dieta
import com.tfg.smartdiet.domain.ResetWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavegador: BottomNavigationView
    private lateinit var db: FirebaseFirestore
    private val logtag = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityMainBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initNavigation()
        db = FirebaseFirestore.getInstance()

        checkAndCreateDieta()
        setResetTrigger(0,0) //Cambiar la hora del reset
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.Main_fragmentcontainerview) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavegador.setupWithNavController(navController)
    }

    private fun setResetTrigger(hours: Int, minutes: Int) {
        val calendar = Calendar.getInstance()

        // Set the reset time (hours and minutes)
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)

        // If the reset time has already passed for today, move it to the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val currentTime = Calendar.getInstance().timeInMillis
        val triggerTimeInMillis = calendar.timeInMillis - currentTime

        // Create the reset worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()

        val resetWorkRequest = OneTimeWorkRequestBuilder<ResetWorker>()
            .setInitialDelay(triggerTimeInMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        // Enqueue the reset work request
        WorkManager.getInstance(this@MainActivity).enqueue(resetWorkRequest)
        Log.d(logtag, "Reset scheduled for ${calendar.time}")
    }

    private fun checkAndCreateDieta() {
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

                    // If dietaActID is null, create a new diet
                    if (dietaActID == null) {
                        createNewDietaForUser(userID)
                        Log.i(logtag, "El usuario $userID no tiene dieta, se crea una nueva")
                    }
                    else{
                        Log.i(logtag, "El usuario $userID ya tiene dieta")
                    }

                }
                .addOnFailureListener { e ->
                    Log.e(logtag, "Error retrieving user document: ${e.message}")
                }
        }
    }


    private fun createNewDietaForUser(userID: String) {
        // Define default values for the new diet
        val defaultCalorias = "2000"
        val defaultProteinas = "100"
        val defaultGrasas = "50"
        val defaultCarbohidratos = "100"

        // Get today's date
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        // Construct the Dieta object
        val newDieta = Dieta(
            caloriasAct = "",
            caloriasObj = defaultCalorias,
            carbohidratosAct = "",
            carbohidratosObj = defaultCarbohidratos,
            grasasAct = "",
            grasasObj = defaultGrasas,
            proteinasAct = "",
            proteinasObj = defaultProteinas,
            fecha = currentDate,
            userID = userID
        )

        // Convert Dieta object to map
        val dietaMap = newDieta.toMap()

        // Add the dieta data to Firestore
        db.collection("dietas")
            .add(dietaMap)
            .addOnSuccessListener { dietaDocRef ->
                Log.i(logtag, "Se ha añadido la dieta al usuario: $userID")

                // Update the user document with the new dietaActID
                updateUserWithDietaActID(userID, dietaDocRef.id)
            }
            .addOnFailureListener { e ->
                Log.e(logtag, "Error al añadir la dieta al usuario: $userID, ${e.message}")
            }
    }


    private fun updateUserWithDietaActID(userID: String, dietaID: String) {
        // Update the user document with the dietaActID
        db.collection("users")
            .document(userID)
            .update("dietaActID", dietaID)
            .addOnSuccessListener {
                Log.i(logtag, "Se ha actualizado el dietaActID del usuario")
            }
            .addOnFailureListener { e ->
                Log.e(logtag, "Error al actualizar dietaActID del usuario: ${e.message}")
            }
    }
}