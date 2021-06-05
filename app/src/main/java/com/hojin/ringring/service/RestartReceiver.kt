package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hojin.ringring.util.DBHelper

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DBHelper(context)
        if(dbHelper.getStatus() != "ON")
            return

        val ACTION_AUTOSTART_SERVICE:String = "android.intent.action.BOOT_COMPLETED"

        val action = intent.getAction()

        if(action.equals(ACTION_AUTOSTART_SERVICE)|| action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_SCREEN_OFF)){
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
