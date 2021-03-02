package com.hojin.ringring.activity

import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hojin.ringring.R
import com.hojin.ringring.util.DBHelper

class WorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent = intent
        val phone_number:String = intent.getStringExtra("call_number").toString()

        Toast.makeText(applicationContext,"착신번호 : "+phone_number,Toast.LENGTH_SHORT).show()

        val dbHelper = DBHelper(this)
        val audioManager : AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if(dbHelper.isKnownNumber(phone_number)){
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }else if(true){//5분안에 2번 같은번호로 오면
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }
    }
}