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
import androidx.appcompat.app.AppCompatActivity
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

        startForegroundService( Intent(this, RingService::class.java))
        btn_getfriend.setOnClickListener {
//            val rt = RingtoneManager.getRingtone(applicationContext,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//            rt.play()
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)  //핸드폰 벨소리 불러와서 알림해주고 싶은데 되려나?
            val path = "/system/media/audio/ringtones/"
            val mAudio = MediaPlayer()
            mAudio.setDataSource(path)
            mAudio.isLooping = true
            mAudio.prepare()
            mAudio.start();


            //getPhoneBook()
        }


    }

    fun getPhoneBook(){
        val dbHelper: DBHelper = DBHelper(this)
        dbHelper.deletePhoneList()

        val contacts:ArrayList<String> = ArrayList()
        var count:Int = 1

        val c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        while(c!!.moveToNext()){
            val contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            contacts.add(count.toString() + ". "+contactName + " : "+phNumber)
            count++

            dbHelper.insertPhoneLIST(contactName,phNumber)
        }
        c.close()

        var adapter = ArrayAdapter<String>(applicationContext, R.layout.text,contacts)
        main_listview.adapter = adapter

    }
}