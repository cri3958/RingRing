package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.hojin.ringring.util.DBHelper
import com.hojin.ringring.util.util

class CallReceiver : BroadcastReceiver() {
    private var PhoneState: String? = null
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE).toString()
        if (state == PhoneState)
            return
        else
            PhoneState = state
        Log.d("CallReceiver", "REEEEEEEEEEEEEEEEECIVE 1 $state")
        if (TelephonyManager.EXTRA_STATE_RINGING == state) {
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)  //일단은 임마가 문제
            if (incomingNumber.isNullOrEmpty()) {
                Log.d("CallReceiver", "REEEEEEEEEEEEEEEEECIVE 2 return ${incomingNumber.toString()}")
                return
            }
            val util = util()
            val phone_number = util.formatNumber(incomingNumber.toString())

            val dbHelper = DBHelper(context)

            if (dbHelper.getStatus() != "ON")
                return

            if (dbHelper.isKnownNumber(phone_number)) {
                changetoRingmode(context)
            } else {//모르는 번호여도 5분내로 2번오면 소리로    >>>테스트는 아직
                if (dbHelper.compareLatestCall(phone_number)) {
                    Log.d("CallReceiver", "모르지만 5분내로 2번 전화옴")
                    changetoRingmode(context)
                } else
                    dbHelper.SettingLatestCall(phone_number)
            }
            val intent: Intent = Intent(context, RingService::class.java)
            context.startForegroundService(intent)
        }
    }
}

fun changetoRingmode(context: Context) {
    val dbHelper = DBHelper(context)
    val status = dbHelper.isWaitingService()
    Log.d("CallReceiver status", status.toString())
    if (!status) {  //서비스 중이 아니다
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        Toast.makeText(context.applicationContext, "소리모드로 변경!", Toast.LENGTH_SHORT).show()
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        audioManager.setStreamVolume(AudioManager.STREAM_RING, ((audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.9).toInt()), AudioManager.FLAG_PLAY_SOUND)
    } else {
        Toast.makeText(context.applicationContext, "서비스 대기시간", Toast.LENGTH_SHORT).show()
    }
}
