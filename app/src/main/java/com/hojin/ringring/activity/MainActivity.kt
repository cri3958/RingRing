package com.hojin.ringring.activity

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hojin.ringring.PhoneBook.PhoneBookActivity
import com.hojin.ringring.R
import com.hojin.ringring.service.RingService
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_timer.view.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.ACCESS_NOTIFICATION_POLICY
    )
    var rejectedPermissionList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkPermissions()) {//요청할 것이 있으면
            requestPermissions()
            /*val builder1 = AlertDialog.Builder(this)
            builder1.setTitle("어플 동작을 위한 권한요청")
            builder1.setMessage("수락을 해주셔야지 어플이 동작해요...")
            val listner1 = DialogInterface.OnClickListener { _, p1 ->
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        requestPermissions()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        Toast.makeText(applicationContext, "그럼 어쩔수없죠... Bye....", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
            builder1.setPositiveButton("좋아요", listner1)
            builder1.setNegativeButton("싫어요", listner1)
            builder1.show()*/
        }

        //방해금지모드 빼곤 다 획득!
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//방해금지모드 퍼미션
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val builder2 = AlertDialog.Builder(this)
            builder2.setMessage("어플 동작을 위한 권한요청")
            val listner = DialogInterface.OnClickListener { _, p1 ->
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        startActivityForResult(intent, 0)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        Toast.makeText(applicationContext, "그럼 어쩔수없죠... Bye....", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
            builder2.setPositiveButton("좋아요", listner)
            builder2.setNegativeButton("싫어요", listner)
            builder2.show()
        }


        //권한 전부다 획득!
        val dbHelper = DBHelper(this)
        val status = dbHelper.getStatus()
        if (status == "ON") {//이미 서비스 중이다
            startForegroundService(Intent(this, RingService::class.java))
            switch_service.isChecked = true
            btn_timer.visibility = View.VISIBLE
        } else if (status == "error") {
            dbHelper.SettingStatus("OFF")
        }

        switch_service.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {//ON
                startForegroundService(Intent(this, RingService::class.java))
                Toast.makeText(applicationContext, "서비스 시작", Toast.LENGTH_SHORT).show()
                dbHelper.SettingStatus("ON")
                btn_timer.visibility = View.VISIBLE
            } else {//OFF
                stopService(Intent(this, RingService::class.java))
                Toast.makeText(applicationContext, "서비스 종료", Toast.LENGTH_SHORT).show()
                dbHelper.SettingStatus("OFF")
                btn_timer.visibility = View.GONE
            }
        }

        btn_timer.setOnClickListener {
            if (!dbHelper.isWaitingService())    //서비스 대기중이 아닐 경우 btn_timer에 서비스 종료시간 표시
                dialog(this)
            else {
                Toast.makeText(applicationContext, "서비스 대기 취소", Toast.LENGTH_SHORT).show()
                dbHelper.SettingTimer(-1)
                Settingbtntext()
            }
        }

        btn_getfriend.setOnClickListener {
            val intent = Intent(this, PhoneBookActivity::class.java)
            startActivity(intent)
        }

        Settingbtntext()

    }

    private fun dialog(context: Context){   //서비스 대기용 dialog 생성 함수
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
        builder.setPositiveButton("확인"){ _, _ ->
            if(view.timer_input.text.isNotEmpty()) {//기타가 아닐경우
                time = Integer.parseInt(view.timer_input.text.toString())
            }
            Toast.makeText(context, time.toString() + "분동안 서비스 대기", Toast.LENGTH_SHORT).show()//이 시간을 btn_time에다가 대체해주면 좋을것도 같은..?
            val dbHelper = DBHelper(this)
            dbHelper.SettingTimer(time)
            Settingbtntext()
        }
        builder.setNegativeButton("취소",null)
        builder.setCancelable(false)
        builder.create()
        builder.show()
    }

    private fun Settingbtntext(){   //서비스 대기시 서비스대기버튼의 text 수정
        val dbHelper = DBHelper(this)
        if(dbHelper.isWaitingService()){//지금이 서비스 대기중이다.
            val text = dbHelper.getTimer()
            if(text != "error")
                btn_timer_text.text = text+"까지\n서비스 대기"
        }else{
            btn_timer_text.text = "서비스 잠시 멈추기"
        }
    }

    private fun checkPermissions():Boolean {//https://m.blog.naver.com/PostView.naver?blogId=chandong83&logNo=221616557088&proxyReferer=https:%2F%2Fwww.google.com%2F
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var isClear: Boolean = true//권한이 전부다 있음
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                Log.d("PERMISsion",permission)
                rejectedPermissionList.add(permission)
                isClear = false
            }
        }
        return isClear
    }

    private fun requestPermissions(){
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }


}
