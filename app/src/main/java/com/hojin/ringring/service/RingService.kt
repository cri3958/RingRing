package com.hojin.ringring.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.hojin.ringring.R
import java.util.*

class RingService : Service() {
    private var mReceiver: ScreenReceiver? = null


    val channelId = "com.suw.lockscreen"
    val channelName = "My service channel"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mReceiver = ScreenReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)
        Toast.makeText(applicationContext,"RingRing 서비스 시작", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initializeNotification(intent)
        registerRestartAlarm(true)
        //Toast.makeText(applicationContext,"RingService onStartCommand", Toast.LENGTH_SHORT).show()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        registerRestartAlarm(true)
        //Toast.makeText(applicationContext,"RingService onDestroy", Toast.LENGTH_SHORT).show()
    }

    fun registerRestartAlarm(isOn:Boolean){
        //Toast.makeText(applicationContext,"RingService registerRestartAlarm", Toast.LENGTH_SHORT).show()
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
    fun initializeNotification(intent: Intent){
        //Toast.makeText(applicationContext,"RingService initializeNotification", Toast.LENGTH_SHORT).show()
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            var manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("title")
            .setContentText("content text")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
        val notification = notificationBuilder.build()
        val NOTIFICATION_ID = 12345
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        //Toast.makeText(applicationContext,"RingService onTaskRemoved", Toast.LENGTH_SHORT).show()
        super.onTaskRemoved(rootIntent)

        var calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        calendar.add(Calendar.SECOND,5)

        val intent: Intent = Intent(this, CallReceiver::class.java)
        val sender: PendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender)
    }
}