package com.hojin.ringring.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.hojin.ringring.R
import java.util.*

class RingService : Service() {
    private lateinit var mReceiver: CallReceiver

    val channelId = "com.suw.lockscreen"
    val channelName = "My service channel"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mReceiver = CallReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)
        Toast.makeText(applicationContext,"RingRing 서비스 시작", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initializeNotification(intent)
        registerRestartAlarm(true)
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        registerRestartAlarm(true)
    }

    fun registerRestartAlarm(isOn:Boolean){
        val intent: Intent = Intent(this,
            RestartReceiver::class.java)
        val sender: PendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if(isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+100,100,sender)
        }else{
            alarmManager.cancel(sender)
        }
    }
    fun initializeNotification(intent: Intent){//?
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Working!!")
            .setContentText("서비스가 정상 작동중입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
        val notification = notificationBuilder.build()
        val NOTIFICATION_ID = 12345
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        calendar.add(Calendar.SECOND,30)

        val intent: Intent = Intent(this, CallReceiver::class.java)
        val sender: PendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender)
    }
}