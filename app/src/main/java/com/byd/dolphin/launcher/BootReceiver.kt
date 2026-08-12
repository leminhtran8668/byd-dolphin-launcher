package com.byd.dolphin.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.byd.dolphin.launcher.data.VehicleDataService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Optionally start data service after boot
            // VehicleDataService.start(context)
        }
    }
}
