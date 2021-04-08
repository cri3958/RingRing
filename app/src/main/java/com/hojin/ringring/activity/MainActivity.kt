package com.hojin.ringring.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hojin.ringring.PhoneBook.PhoneBookActivity
import com.hojin.ringring.R
import com.hojin.ringring.service.RingService
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(!notificationManager.isNotificationPolicyAccessGranted()) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent,0)
        }

        btn_getfriend.setOnClickListener {
            val intent = Intent(this,PhoneBookActivity::class.java)
            startActivity(intent)
        }

        switch_service.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                startForegroundService( Intent(this, RingService::class.java))
                Toast.makeText(applicationContext,"서비스 시작",Toast.LENGTH_SHORT).show()
            }else{
                stopService(Intent(this,RingService::class.java))
                Toast.makeText(applicationContext,"서비스 종료",Toast.LENGTH_SHORT).show()
            }
        }
    }
}