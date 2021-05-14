package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hojin.ringring.activity.RestartserviceActivity

class AutoStartReceiver : BroadcastReceiver() {//재부팅시 동작
override fun onReceive(context: Context, intent: Intent) {
    val ACTION_AUTOSTART_SERVICE: String = "android.intent.action.BOOT_COMPLETED"
    Toast.makeText(context.getApplicationContext(), "working AUTOSTART SERVICE : " + intent.getAction().toString(), Toast.LENGTH_SHORT).show()
    val action = intent.getAction()
    if (action.equals(ACTION_AUTOSTART_SERVICE) || action.equals(Intent.ACTION_SCREEN_OFF)) {
        val intent: Intent = Intent(context.applicationContext, RingService::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startForegroundService(intent)


    } else {
        Toast.makeText(context, "ENTER AUTOSTART SERIVCE BUT DIFFERENT ACTION", Toast.LENGTH_SHORT)
            .show()
    }
}
}
