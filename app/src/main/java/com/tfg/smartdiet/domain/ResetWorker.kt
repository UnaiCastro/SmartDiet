package com.tfg.smartdiet.domain

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ResetWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Perform the reset operation here
        setResetFlag()
        return Result.success()
    }

    private fun setResetFlag() {
        val sharedPreferences =
            applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("reset", true)
        editor.apply()

        Log.d("ResetWorker", "Reset flag set")
    }
}
