package com.example.apptracker.util.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackedAppOpenedStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val database = getDatabase(applicationContext)
            val manager = TrackedAppsManager(database, applicationContext)

            manager.updateTrackedAppsOpenedStatus()
        } catch (error: java.lang.Exception) {
            return Result.failure()
        }
        return Result.success()
    }

}