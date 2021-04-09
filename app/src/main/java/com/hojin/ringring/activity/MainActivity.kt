package com.hojin.ringring.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hojin.ringring.PhoneBook.PhoneBookActivity
import com.hojin.ringring.R
import com.hojin.ringring.service.RingService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    val fileName:String = "servecing.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(!notificationManager.isNotificationPolicyAccessGranted()) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent,0)
        }

        val file = File("/data/data/com.hojin.ringring/files/"+fileName)
        if(!file.exists()){//파일이 없으면
            val outFs: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            val str="OFF@"
            outFs.write(str.toByteArray())
            outFs.close()
        }else{//파일이 있으면
            val inFs: FileInputStream = openFileInput(fileName)
            var txt = ByteArray(10)
            inFs.read(txt)

            val temp = String(txt).split("@")
            if(temp[0].equals("ON")){
                startForegroundService( Intent(this, RingService::class.java))
                switch_service.isChecked = true
            }
        }

        btn_getfriend.setOnClickListener {
            val intent = Intent(this,PhoneBookActivity::class.java)
            startActivity(intent)
        }

        switch_service.setOnCheckedChangeListener { compoundButton, isChecked ->
            val outFs: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            var str = ""
            if(isChecked){//ON
                startForegroundService( Intent(this, RingService::class.java))
                Toast.makeText(applicationContext,"서비스 시작",Toast.LENGTH_SHORT).show()
                str = "ON@"
            }else{//OFF
                stopService(Intent(this,RingService::class.java))
                Toast.makeText(applicationContext,"서비스 종료",Toast.LENGTH_SHORT).show()
                str = "OFF@"
            }
            outFs.write(str.toByteArray())
            outFs.close()
        }
    }
}