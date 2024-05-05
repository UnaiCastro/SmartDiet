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
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityMainBinding
import com.tfg.smartdiet.domain.ResetWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavegador: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityMainBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initNavigation()
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
        Log.d("MainActivity", "Reset scheduled for ${calendar.time}")
    }
}