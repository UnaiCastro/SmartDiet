package com.tfg.smartdiet.iu.PaginaPrincipal

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.domain.Dieta
import com.tfg.smartdiet.domain.ResetWorker
import com.tfg.smartdiet.iu.InicioSesion.InicioSesionActivity
import com.tfg.smartdiet.iu.PaginaPrincipal.Historico.HistoricoActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.tfg.smartdiet.iu.Bienvenida.BienvenidaActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavegador: BottomNavigationView
    private lateinit var db: FirebaseFirestore
    private val logtag = "MainActivity"
    private val CHANNEL_ID = "123"
    private lateinit var auth:FirebaseAuth
    private var dietCreationInitiated = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(logtag, "MainActivity: onCreate")
        val conf = ConfigUsuario(getSharedPreferences("Configuracion", MODE_PRIVATE))
        conf.initTema()
//        conf.initIdioma(applicationContext)
        binding =
            ActivityMainBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initNavigation()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        // Retrieve the flag value
        dietCreationInitiated = sharedPreferences.getBoolean("dietCreationInitiated", false)

        configureUI()

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "notificaciones"
            val descriptionText = "canal de notificaciones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        //setResetTrigger(0,0) //Cambiar la hora del reset
    }

    override fun onStart() {
        super.onStart()
        Log.i(logtag, "MainActivity: onStart")
        db = FirebaseFirestore.getInstance()
        Log.i(logtag, "MainActivity: onStart, flag dietCreationInitiated: $dietCreationInitiated")

        if (!dietCreationInitiated) {
            dietCreationInitiated = true
            checkAndHandleActiveDiet()
            // Save the updated flag value in SharedPreferences
            editor = sharedPreferences.edit()
            editor.putBoolean("dietCreationInitiated", dietCreationInitiated)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(logtag, "MainActivity: onResume")
    }

    private fun configureUI() {
        binding.topAppBar.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId) {
                R.id.historicoDieta -> {
                    // Activity
                    HistoricoActivity.navigateTo(this)
                    true
                }
                R.id.gestionDietas -> {
                    // Fragment
                    NavHostFragment.findNavController(binding.MainFragmentcontainerview.getFragment()).navigate(R.id.actionGestionDietas)
                    true
                }
                R.id.recursosRecetas -> {
                    // Fragment
                    NavHostFragment.findNavController(binding.MainFragmentcontainerview.getFragment()).navigate(R.id.actionDetalleRecetaPorridge)
                    true
                }
                R.id.vistaAlimentos -> {
                    // Fragment
                    NavHostFragment.findNavController(binding.MainFragmentcontainerview.getFragment()).navigate(R.id.actionVistaAlimentos)
                    true
                }
                R.id.cerrarSesion -> {
                    // Activity
                    val conf =
                        this?.let { ConfigUsuario(it.getSharedPreferences("Configuracion", Context.MODE_PRIVATE)) }
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val i= Intent(this,BienvenidaActivity::class.java)
                    startActivity(i)
                    conf?.cerrarSesion()
                    true
                } else -> {
                    false
                }
            }
        }
    }



    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.Main_fragmentcontainerview) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavegador.setupWithNavController(navController)
    }

    //este reset no se usa
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

    private fun checkAndHandleActiveDiet() {
        // Get the current userid
        val userID = FirebaseAuth.getInstance().currentUser?.uid


        // Ensure that current user is not null
        userID?.let { userID ->
            // Get today's date
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

            // Get the user document reference
            val userDocRef = db.collection("users").document(userID)

            // Retrieve the dietaActID from the user document
            userDocRef.get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    val dietaActID = userDocumentSnapshot.getString("dietaActID")

                    // If dietaActID is null, create a new diet
                    if (dietaActID == null) {
                        createNewDietaForUser()
                        Log.i(logtag, "El usuario ${userID} no tiene dieta, se crea una nueva")
                    } else {
                        // Retrieve the dieta document
                        db.collection("dietas").document(dietaActID).get()
                            .addOnSuccessListener { dietaDocumentSnapshot ->
                                val dietaFecha = dietaDocumentSnapshot.getString("fecha")

                                // Check if the dieta's date matches today's date
                                if (dietaFecha == currentDate) {
                                    Log.i(logtag, "La dieta activa para el usuario ${userID} es de hoy.")
                                    setDietCreationInitiatedToFalse()
                                } else {
                                    Log.i(logtag, "La dieta activa para el usuario ${userID} no es de hoy, guardándola y creando nueva.")
                                    handleExpiredActiveDiet(userID, dietaActID)

                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(logtag, "Error retrieving dieta document: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(logtag, "Error retrieving user document: ${e.message}")
                }
        } ?: run {
            Log.e(logtag, "Current user is null.")
        }
    }

    private fun handleExpiredActiveDiet(userID: String, oldDietaID: String) {
        // Get the objective values of the expired diet
        db.collection("dietas").document(oldDietaID).get()
            .addOnSuccessListener { dietaDocumentSnapshot ->
                val previousDietaValues = dietaDocumentSnapshot.data?.mapValues { it.value.toString() } ?: mapOf()

                // Get today's date
                val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                // Construct the new Dieta object with the same objective values
                val newDieta = Dieta(
                    caloriasAct = "0",
                    caloriasObj = previousDietaValues["caloriasObj"] ?: "0",
                    carbohidratosAct = "0",
                    carbohidratosObj = previousDietaValues["carbohidratosObj"] ?: "0",
                    grasasAct = "0",
                    grasasObj = previousDietaValues["grasasObj"] ?: "0",
                    proteinasAct = "0",
                    proteinasObj = previousDietaValues["proteinasObj"] ?: "0",
                    fecha = currentDate,
                    userID = userID
                )

                // Convert Dieta object to map
                val dietaMap = newDieta.toMap()

                // Add the new dieta data to Firestore
                db.collection("dietas")
                    .add(dietaMap)
                    .addOnSuccessListener { newDietaDocRef ->
                        Log.i(logtag, "Se ha creado una nueva dieta para el usuario $userID con los mismos valores objetivos que la dieta expirada.")

                        // Update the user document with the new active dieta ID
                        updateUserWithDietaActID(userID, newDietaDocRef.id)
                        val conf = ConfigUsuario(getSharedPreferences("Configuracion", Context.MODE_PRIVATE))
                        if(conf.getNotis()=="ON") {
                            // Send local notification with previous dieta values
                            sendLocalNotification(previousDietaValues)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(logtag, "Error al añadir la nueva dieta para el usuario $userID: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(logtag, "Error retrieving expired dieta document: ${e.message}")
            }
    }


    private fun sendLocalNotification(previousDietaValues: Map<String, String>) {
        // Extract the date from the previous dieta values
        val oldDietaDate = previousDietaValues["fecha"] ?: ""

        // Format the notification content
        val notificationContent = buildString {
            append(getString(R.string.calorias) + ": ${previousDietaValues["caloriasAct"]}/${previousDietaValues["caloriasObj"]}\n")
            append(getString(R.string.proteinas) + ": ${previousDietaValues["proteinasAct"]}/${previousDietaValues["proteinasObj"]}\n")
            append(getString(R.string.grasas) + ": ${previousDietaValues["grasasAct"]}/${previousDietaValues["grasasObj"]}\n")
            append(getString(R.string.carbs) + ": ${previousDietaValues["carbohidratosAct"]}/${previousDietaValues["carbohidratosObj"]}")
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_info)
            .setContentTitle(getString(R.string.tituloNotificacion) + " $oldDietaDate")
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        val notificationId = 100
        val context = applicationContext
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Handle the case where the permission is not granted
                // You can request permission here or handle it in the calling component
                return
            }
            notify(notificationId, notificationBuilder.build())
        }
    }


    private fun createNewDietaForUser() {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val userID = user.uid
            // Fetch user attributes from Firestore or wherever they are stored
            // For example, you might have a 'users' collection in Firestore where user details are stored
            val userDocRef = db.collection("users").document(userID)

            // Retrieve user attributes
            userDocRef.get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    val peso = userDocumentSnapshot.getString("peso") // Assuming weight is stored as a string
                    val objetivo = userDocumentSnapshot.getString("objetivo")
                    val genero = userDocumentSnapshot.getString("genero")
                    Log.d(logtag, "Peso usuario: $peso")
                    Log.d(logtag, "Peso objetivo: $objetivo")
                    Log.d(logtag, "Peso genero: $genero")


                    if (peso != null && objetivo != null && genero != null) {
                        val goals = calculateGoals(peso, genero, objetivo)
                        Log.i(logtag, "Calculated Goals: $goals")

                        // Create the new dieta with calculated goals
                        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                        val newDieta = Dieta(
                            caloriasAct = "0",
                            caloriasObj = goals["caloriasObj"].toString(),
                            carbohidratosAct = "0",
                            carbohidratosObj = goals["carbohidratosObj"].toString(),
                            grasasAct = "0",
                            grasasObj = goals["grasasObj"].toString(),
                            proteinasAct = "0",
                            proteinasObj = goals["proteinasObj"].toString(),
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

                                // Reset the flag to false
                                setDietCreationInitiatedToFalse()
                            }
                            .addOnFailureListener { e ->
                                Log.e(logtag, "Error al añadir la dieta al usuario: $userID, ${e.message}")
                            }
                    } else {
                        Log.e(logtag, "One or more user attributes are null.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(logtag, "Error retrieving user attributes: ${e.message}")
                }
        } ?: run {
            Log.e(logtag, "Current user is null.")
        }
    }




    private fun updateUserWithDietaActID(userID: String, dietaID: String) {
        // Update the user document with the dietaActID
        db.collection("users")
            .document(userID)
            .update("dietaActID", dietaID)
            .addOnSuccessListener {
                Log.i(logtag, "Se ha actualizado el dietaActID del usuario")

                setDietCreationInitiatedToFalse()

                reloadFragment()
            }
            .addOnFailureListener { e ->
                Log.e(logtag, "Error al actualizar dietaActID del usuario: ${e.message}")
            }
    }
    private fun reloadFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.Main_fragmentcontainerview) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(navController.graph.id)
    }

    private fun setDietCreationInitiatedToFalse() {
        // Reset the flag to false
        dietCreationInitiated = false
        // Save the updated flag value in SharedPreferences
        editor = sharedPreferences.edit()
        editor.putBoolean("dietCreationInitiated", dietCreationInitiated)
        editor.apply()
    }

    private fun calculateGoals(weight: String, gender: String, objective: String): Map<String, Int> {
        val weightKg = weight.substringBefore(" kg").toInt()

        val calorieGoal: Int
        val proteinGoal: Int
        val fatGoal: Int
        val carbGoal: Int

        when {
            objective == "Adelgazar" -> {
                calorieGoal = (weightKg * 22.0).toInt()
                proteinGoal = (weightKg * 2.2).toInt()
                fatGoal = (calorieGoal * 0.25 / 9).toInt()
                carbGoal = ((calorieGoal - (proteinGoal * 4) - (fatGoal * 9)) / 4).toInt()
            }
            objective == "Aumentar volumen" -> {
                calorieGoal = (weightKg * 24.0).toInt()
                proteinGoal = (weightKg * 2.5).toInt()
                fatGoal = (calorieGoal * 0.3 / 9).toInt()
                carbGoal = ((calorieGoal - (proteinGoal * 4) - (fatGoal * 9)) / 4).toInt()
            }
            else -> { // Estar en forma
                calorieGoal = (weightKg * 23.0).toInt()
                proteinGoal = (weightKg * 2.0).toInt()
                fatGoal = (calorieGoal * 0.3 / 9).toInt()
                carbGoal = ((calorieGoal - (proteinGoal * 4) - (fatGoal * 9)) / 4).toInt()
            }
        }

        return mapOf(
            "caloriasObj" to calorieGoal,
            "proteinasObj" to proteinGoal,
            "grasasObj" to fatGoal,
            "carbohidratosObj" to carbGoal
        )
    }

}