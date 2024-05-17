package com.tfg.smartdiet.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R

class HoyWidget: AppWidgetProvider() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.hoy_widget)

        // Obtén los datos de Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val correo = auth.currentUser?.email

        db.collection("users")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val dietaActID = documents.documents[0].getString("dietaActID")
                    dietaActID?.let {
                        db.collection("dietas")
                            .document(it)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val caloriasAct = document.getString("caloriasAct")?.toIntOrNull() ?: 0
                                    val caloriasObj = document.getString("caloriasObj")?.toIntOrNull() ?: 0

                                    views.setTextViewText(R.id.textViewCalorias, "Calorías: $caloriasAct/$caloriasObj")
                                    views.setProgressBar(R.id.progressBarCalorias, caloriasObj, caloriasAct, false)

                                    // Actualiza el widget
                                    appWidgetManager.updateAppWidget(appWidgetId, views)
                                }
                            }
                    }
                }
            }

        // Establece el PendingIntent para actualizar el widget cuando se haga clic
        val intent = Intent(context, HoyWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.textViewCalorias, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
   /* private val ACTION_UPDATE_WIDGET = "com.example.cinemapp.action.UPDATE_WIDGET"
    private lateinit var views:RemoteViews
    private var calorias = 0
    private var proteinas = 0
    private var grasas = 0
    private var carbohidratos = 0

    private var MAX_CALORIAS_DIARIAS=100
    private var MAX_PROTEINAS_DIARIAS=100
    private var MAX_GRASAS_DIARIAS=100
    private var MAX_CARBOHIDRATOS_DIARIOS=100
    private lateinit var recyclerViewEntries: RecyclerView

    private val allEntries: MutableList<HashMap<String, Any>> = mutableListOf()

    private val entryIds: MutableList<String> = mutableListOf()


    private lateinit var db:FirebaseFirestore

    private var dietaActID: String? = null

    private lateinit var auth: FirebaseAuth
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_UPDATE_WIDGET == intent.action) {
            // Se recibió una acción para actualizar el widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    com.tfg.smartdiet.widget.HoyWidget::class.java))
        }
    }


    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Este método se llama cuando se crea el primer widget

        // Obtén el AppWidgetManager para interactuar con los widgets
        val appWidgetManager = AppWidgetManager.getInstance(context)

        // Obten los IDs de los widgets activos actualmente
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, com.tfg.smartdiet.widget.HoyWidget::class.java)
        )
        views = RemoteViews(context.packageName, R.layout.hoy_widget)
        obtenerDietaActual { dietaActID ->
            this.dietaActID = dietaActID
            dietaActID?.let {
                setDietaObjectives(it)
                setCurrentValues(it)
                fetchEntries(it)
            }
            initRecyclerView()
            initUI()
            initListener()
            checkReset()
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

    private fun initUI() {
        // Actualiza la barra de progreso para las calorías
        views. progressBarCalorias.progress = calcularProgreso(calorias, MAX_CALORIAS_DIARIAS)
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

    private fun checkReset(context:Context){
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
    }*/
}