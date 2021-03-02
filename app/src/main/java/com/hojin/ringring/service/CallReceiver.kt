package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.hojin.ringring.util.DBHelper

class CallReceiver : BroadcastReceiver(){
    private var PhoneState:String? = null
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if(state.equals(PhoneState))
            return
        else
            PhoneState = state

        if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)  //일단은 임마가 문제
            Log.d("@@@@@",incomingNumber.toString())
            val phone_number = PhoneNumberUtils.formatNumber(incomingNumber)

            /*val intent = Intent(context, WorkActivity::class.java)
            Log.d("RECEIVE NUMBER",phone_number)
            intent.putExtra("call_number",phone_number)
            context.startForegroundService(intent)*/

            val dbHelper = DBHelper(context)
            val audioManager : AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if(dbHelper.isKnownNumber(phone_number)){
                Toast.makeText(context.applicationContext,"소리모드로 변경!",Toast.LENGTH_SHORT).show()
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }else if(true){//5분안에 2번 같은번호로 오면
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
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
}