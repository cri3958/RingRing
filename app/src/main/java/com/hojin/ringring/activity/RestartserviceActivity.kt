package com.hojin.ringring.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hojin.ringring.R
import com.hojin.ringring.service.RingService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class RestartserviceActivity : AppCompatActivity() {
    val fileName:String = "servecing.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restartservice)

        val inFs: FileInputStream = openFileInput(fileName)
        var txt = ByteArray(10)
        inFs.read(txt)

        val temp = String(txt).split("@")
        if (temp[0].equals("ON")) {
            startForegroundService(Intent(this, RingService::class.java))
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O_MR1){
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }
}