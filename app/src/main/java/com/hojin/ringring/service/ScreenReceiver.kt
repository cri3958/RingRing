package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hojin.ringring.util.DBHelper

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DBHelper(context)
        if(dbHelper.getStatus() != "ON")
            return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val intent:Intent = Intent(context, RingService::class.java)
                context.startForegroundService(intent)
            } else{
                val intent:Intent = Intent(context, RingService::class.java)
                context.startService(intent)
            }
        }
    }
}