package com.tfg.smartdiet.iu.Widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tfg.smartdiet.R

class HoyWidget: AppWidgetProvider() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var listenerRegistration: ListenerRegistration? = null

    override fun onEnabled(context: Context) {
        // Inicializa recursos aquí si es necesario
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Escuchar cambios en tiempo real para el usuario actual
        val correo = auth.currentUser?.email
        db.collection("users")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val dietaActID = documents.documents[0].getString("dietaActID")
                    dietaActID?.let {
                        listenerRegistration = db.collection("dietas")
                            .document(it)
                            .addSnapshotListener { documentSnapshot, e ->
                                if (e != null) {
                                    // Manejar error
                                    return@addSnapshotListener
                                }
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    val appWidgetManager = AppWidgetManager.getInstance(context)
                                    val appWidgetIds = appWidgetManager.getAppWidgetIds(
                                        ComponentName(context, HoyWidget::class.java)
                                    )
                                    for (appWidgetId in appWidgetIds) {
                                        updateWidget(context, appWidgetManager, appWidgetId, documentSnapshot)
                                    }
                                }
                            }
                    }
                }
            }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, null)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, documentSnapshot: DocumentSnapshot?) {
        val views = RemoteViews(context.packageName, R.layout.hoy_widget)

        if (documentSnapshot != null) {
            val caloriasAct = documentSnapshot.getString("caloriasAct")?.toIntOrNull() ?: 0
            val caloriasObj = documentSnapshot.getString("caloriasObj")?.toIntOrNull() ?: 0
            val proteinasAct = documentSnapshot.getString("proteinasAct")?.toIntOrNull() ?: 0
            val grasasAct = documentSnapshot.getString("grasasAct")?.toIntOrNull() ?: 0
            val carbohidratosAct = documentSnapshot.getString("carbohidratosAct")?.toIntOrNull() ?: 0
            val proteinasObj = documentSnapshot.getString("proteinasObj")?.toIntOrNull() ?: 0
            val grasasObj = documentSnapshot.getString("grasasObj")?.toIntOrNull() ?: 0
            val carbohidratosObj = documentSnapshot.getString("carbohidratosObj")?.toIntOrNull() ?: 0

            views.setTextViewText(R.id.textViewCalorias, "$caloriasAct/$caloriasObj")
            views.setProgressBar(R.id.progressBarCalorias, caloriasObj, caloriasAct, false)
            views.setTextViewText(R.id.textViewCaloriasName, "${context.getString(R.string.calorias)}:")
            views.setTextViewText(R.id.textViewProteinas, "$proteinasAct/$proteinasObj")
            views.setProgressBar(R.id.progressBarProteinas, proteinasObj, proteinasAct, false)
            views.setTextViewText(R.id.textViewProteinasName, "${context.getString(R.string.proteinas)}:")
            views.setTextViewText(R.id.textViewGrasas, "$grasasAct/$grasasObj")
            views.setProgressBar(R.id.progressBarGrasas, grasasObj, grasasAct, false)
            views.setTextViewText(R.id.textViewGrasasName, "${context.getString(R.string.grasas)}:")
            views.setTextViewText(R.id.textViewCarbohidratos, "$carbohidratosAct/$carbohidratosObj")
            views.setProgressBar(R.id.progressBarCarbohidratos, carbohidratosObj, carbohidratosAct, false)
            views.setTextViewText(R.id.textViewCarbohidratosName, "${context.getString(R.string.carbs)}:")
        }

        // Actualiza el widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onDisabled(context: Context) {
        // Libera recursos aquí si es necesario
        listenerRegistration?.remove()
    }
}
