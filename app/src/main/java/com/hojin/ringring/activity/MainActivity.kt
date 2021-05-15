package com.hojin.ringring.activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hojin.ringring.PhoneBook.PhoneBookActivity
import com.hojin.ringring.R
import com.hojin.ringring.service.RingService
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_timer.*
import kotlinx.android.synthetic.main.dialog_timer.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


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
            btn_timer.visibility = View.GONE
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
                btn_timer.visibility = View.VISIBLE
            }
        }

        switch_service.setOnCheckedChangeListener { compoundButton, isChecked ->
            val outFs: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            var str = ""
            if(isChecked){//ON
                startForegroundService( Intent(this, RingService::class.java))
                Toast.makeText(applicationContext,"서비스 시작",Toast.LENGTH_SHORT).show()
                str = "ON@"
                btn_timer.visibility = View.VISIBLE
            }else{//OFF
                stopService(Intent(this,RingService::class.java))
                Toast.makeText(applicationContext,"서비스 종료",Toast.LENGTH_SHORT).show()
                str = "OFF@"
                btn_timer.visibility = View.GONE
            }
            outFs.write(str.toByteArray())
            outFs.close()
        }

        btn_timer.setOnClickListener { dialog(this) }

        btn_getfriend.setOnClickListener {
            val intent = Intent(this,PhoneBookActivity::class.java)
            startActivity(intent)
        }
    }

    fun dialog(context: Context){
        val list_of_text = arrayOf("10분","30분","1시간","2시간","3시간","직접입력")
        val list_of_time = arrayOf(10,30,60,120,180)
        var time:Int=0

        val view:View = layoutInflater.inflate(R.layout.dialog_timer,null)


        view.timer_spinner.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list_of_text)
        view.timer_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                time=0
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position!=5) { //값이 "기타"이지 않으면
                    time = list_of_time[position]
                    view.timer_input.text = null
                    view.timer_input.visibility = View.GONE
                }else{
                    view.timer_input.visibility = View.VISIBLE
                }
            }
        }

        val builder:AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle("서비스 잠시 멈추기")
        builder.setView(view)
        builder.setPositiveButton("확인"){dialog,which ->
            if(!view.timer_input.text.isEmpty()) {//기타가 아닐경우
                time = Integer.parseInt(view.timer_input.text.toString())
            }
            Toast.makeText(context, time.toString() + "분동안 서비스 대기", Toast.LENGTH_SHORT).show()//이 시간을 btn_time에다가 대체해주면 좋을것도 같은..?
            val dbHelper = DBHelper(this)
            dbHelper.SettingTimer(time)
        }
        builder.setNegativeButton("취소",null)
        builder.setCancelable(false)
        builder.create()

        builder.show()
    }
}
