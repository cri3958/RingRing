package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hojin.ringring.util.DBHelper

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DBHelper(context)
        if(dbHelper.getStatus() != "ON")
            return

        val ACTION_AUTOSTART_SERVICE:String = "android.intent.action.BOOT_COMPLETED"

        val action = intent.action.toString()

        if (action == ACTION_AUTOSTART_SERVICE || action == Intent.ACTION_BOOT_COMPLETED ) {
            val intent: Intent = Intent(context, RingService::class.java)
            context.startForegroundService(intent)
        }
    }
}
