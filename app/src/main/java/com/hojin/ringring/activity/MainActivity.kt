package com.hojin.ringring.activity

import android.content.Intent
import android.os.Bundle
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
        startForegroundService( Intent(this, RingService::class.java))
        btn_getfriend.setOnClickListener {
            getPhoneBook()
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