package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hojin.ringring.activity.RestartserviceActivity

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            val i = Intent(context, RestartserviceActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val intent:Intent = Intent(context, RingService::class.java)
            context.startForegroundService(intent)
        } else{
            val intent:Intent = Intent(context, RingService::class.java)
            context.startService(intent)
        }
    }
}