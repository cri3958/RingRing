package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hojin.ringring.util.DBHelper


class AutoStartReceiver : BroadcastReceiver() {
    //재부팅시 동작
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DBHelper(context)
        if(dbHelper.getStatus() != "ON")
            return

        val ACTION_AUTOSTART_SERVICE: String = "android.intent.action.BOOT_COMPLETED"
        val action = intent.action.toString()
        if (action == ACTION_AUTOSTART_SERVICE) {
            val intent: Intent = Intent(context.applicationContext, RingService::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startForegroundService(intent)
        }
    }
}
