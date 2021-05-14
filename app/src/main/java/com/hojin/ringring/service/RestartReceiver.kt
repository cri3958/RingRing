package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.hojin.ringring.activity.RestartserviceActivity

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val ACTION_AUTOSTART_SERVICE:String = "android.intent.action.BOOT_COMPLETED"

        val action = intent.getAction()

        if(action.equals(ACTION_AUTOSTART_SERVICE)|| action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_SCREEN_OFF)){
            Toast.makeText(context,action.toString()+" ?= "+ACTION_AUTOSTART_SERVICE+" || Intent.ACTION_BOOT_COMPLETED", Toast.LENGTH_SHORT).show()
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
