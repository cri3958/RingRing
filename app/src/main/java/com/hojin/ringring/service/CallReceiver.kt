package com.hojin.ringring.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hojin.ringring.util.DBHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CallReceiver : BroadcastReceiver(){
    private var PhoneState:String? = null
    val fileName:String = "recently_call.txt"
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if(state.equals(PhoneState))
            return
        else
            PhoneState = state

        if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)  //일단은 임마가 문제
            Log.d("@@@1",incomingNumber.toString())
            if(incomingNumber.isNullOrEmpty())
                return
            val phone_number = formatNumber(incomingNumber.toString())
            Log.d("@@@12",phone_number)

            val dbHelper = DBHelper(context)

            if(dbHelper.isKnownNumber(phone_number)){
                changetoRingmode(context)
            }else if(false){//모르는 번호여도 5분내로 2번오면 소리로    >>>테스트는 아직

                val file = File("/data/data/com.hojin.ringring/files/"+fileName)
                if(file.exists()){//파일이 있으면 같은값인지 확인하고 소리모드로
                    val time = LocalDateTime.now()
                    val datetime = time.format(DateTimeFormatter.ofPattern("MM@dd@HH@mm"))
                    val temp = datetime.split("@")
                    val timedata = temp[3].toInt() + temp[2].toInt()*60 + temp[1].toInt()*60*12 + temp[0].toInt()*60*12*30

                    val inFs: FileInputStream = context.openFileInput(fileName)
                    val txt = ByteArray(500)
                    inFs.read(txt)
                    val fdata = String(txt).trim().split("@")

                    if(fdata[1].equals(phone_number) && fdata[0].toInt()-timedata<=5){ //새로온 전화가 최근 전화와 같은 번호이면서 최근 전화와 5분 이내로 차이나는지 확인
                        changetoRingmode(context)
                    } else{
                        val outFs:FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                        val str: String = "$timedata@$phone_number" //시간을 분으로한거 @ 폰번호
                        outFs.write(str.toByteArray())
                        outFs.close()
                    }
                    inFs.close()
                }else{//파일이 없으면 지금온 전화와 시간 저장하기
                    val time = LocalDateTime.now()
                    val datetime = time.format(DateTimeFormatter.ofPattern("MM@dd@HH@mm"))
                    val temp = datetime.split("@")
                    val timedata = temp[3].toInt() + temp[2].toInt()*60 + temp[1].toInt()*60*12 + temp[0].toInt()*60*12*30

                    val outFs:FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    val str: String = "$timedata@$phone_number" //시간을 분으로한거 @ 폰번호
                    outFs.write(str.toByteArray())
                    outFs.close()
                }
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

    fun changetoRingmode(context: Context){
        val audioManager : AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        Toast.makeText(context.applicationContext,"소리모드로 변경!",Toast.LENGTH_SHORT).show()
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        audioManager.setStreamVolume(AudioManager.STREAM_RING,((audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)*0.9).toInt()),AudioManager.FLAG_PLAY_SOUND)
    }

    //함수 바꾸곤 미확인
    fun formatNumber(number: String): String {  //https://ko.wikipedia.org/wiki/%EB%8C%80%ED%95%9C%EB%AF%BC%EA%B5%AD%EC%9D%98_%EC%A0%84%ED%99%94%EB%B2%88%ED%98%B8_%EC%B2%B4%EA%B3%84
       //앞에가 010,011,016~~~이면 하나, 02,031,032~~~이면 하나 ~~~~ >> 이외는 번호취급 안함... 이정도면 잡겠지
        var returnstr:String
        val numberarray = number.split("")

        if(number.length == 11) {   //01X-XXXX-XXXX || 031~064-XXXX-XXXX
            returnstr = numberarray[1]+numberarray[2]+numberarray[3]+"-"+numberarray[4]+numberarray[5]+numberarray[6]+numberarray[7]+"-"+numberarray[8]+numberarray[9]+numberarray[10]+numberarray[11]
            return returnstr
        }else if(number.length == 10){  //02-XXXX-XXXX || 031~064-XXX-XXXX
            if(numberarray[2].equals("2")){
                returnstr = numberarray[1]+numberarray[2]+"-"+numberarray[3]+numberarray[4]+numberarray[5]+numberarray[6]+"-"+numberarray[7]+numberarray[8]+numberarray[9]+numberarray[10]
                return returnstr
            }else{
                returnstr = numberarray[1]+numberarray[2]+numberarray[3]+"-"+numberarray[4]+numberarray[5]+numberarray[6]+"-"+numberarray[7]+numberarray[8]+numberarray[9]+numberarray[10]
                return returnstr
            }
        } else if(number.length == 9){  //02-XXX-XXXX
            returnstr = numberarray[1]+numberarray[2]+"-"+numberarray[3]+numberarray[4]+numberarray[5]+"-"+numberarray[6]+numberarray[7]+numberarray[8]+numberarray[9]
            return returnstr
        }
        return ""

    }
}