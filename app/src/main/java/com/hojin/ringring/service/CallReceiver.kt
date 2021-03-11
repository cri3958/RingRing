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
import java.util.*

class CallReceiver : BroadcastReceiver(){
    private var PhoneState:String? = null
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if(state.equals(PhoneState))
            return
        else
            PhoneState = state

        if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
            //context.startForegroundService(Intent(context,ScreeningService::class.java))
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)  //일단은 임마가 문제
            Log.d("@@@1",incomingNumber.toString())
            if(incomingNumber.isNullOrEmpty())
                return
            //val phone_number = formatNumber(PhoneNumberUtils.formatNumber(incomingNumber))
            val phone_number = formatNumber(incomingNumber.toString())
            Log.d("@@@12",phone_number.toString())
            /*val intent = Intent(context, WorkActivity::class.java)
            Log.d("RECEIVE NUMBER",phone_number)
            intent.putExtra("call_number",phone_number)
            context.startForegroundService(intent)*/

            val dbHelper = DBHelper(context)
            val audioManager : AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if(dbHelper.isKnownNumber(phone_number)){
                Toast.makeText(context.applicationContext,"소리모드로 변경!",Toast.LENGTH_SHORT).show()
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                //Unable to start receiver com.hojin.ringring.service.CallReceiver: java.lang.SecurityException: Not allowed to change Do Not Disturb state
            }else if(false){//5분안에 2번 같은번호로 오면
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

    fun formatNumber(number: String): String {//다른것도 어떻게할지 생각해보긴 해야됨, 노가다할수도 있긴함
        //앞에가 010,011,016~~~이면 하나, 02,031,032~~~이면 하나 ~~~~
        var returnstr:String
        if(number.length == 11){
            val numberarray = number.toCharArray()
            Log.d("???",numberarray.toString())
            returnstr = numberarray[0].toString()+numberarray[1].toString()+numberarray[2].toString()+"-"+numberarray[3].toString()+numberarray[4].toString()+numberarray[5].toString()+numberarray[6].toString()+"-"+numberarray[7].toString()+numberarray[8].toString()+numberarray[9].toString()+numberarray[10].toString()
            return returnstr
        }
        return ""

    }
}