package com.byd.dolphin.launcher.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.byd.dolphin.launcher.R
import kotlinx.coroutines.*

/**
 * Lightweight foreground service that periodically refreshes vehicle data.
 * Useful if you want widgets or persistent dashboard while other apps are open.
 */
class VehicleDataService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var repo: VehicleRepository

    override fun onCreate() {
        super.onCreate()
        repo = VehicleRepository(applicationContext)
        createChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        scope.launch {
            while (isActive) {
                repo.refresh()
                delay(2000L) // 2s polling – tune as needed
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Vehicle Data",
            NotificationManager.IMPORTANCE_LOW
        )
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Đang cập nhật dữ liệu xe…")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "vehicle_data"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val i = Intent(context, VehicleDataService::class.java)
            context.startForegroundService(i)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, VehicleDataService::class.java))
        }
    }
}
