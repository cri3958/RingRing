package com.hojin.ringring.PhoneBook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hojin.ringring.R
import com.hojin.ringring.model.Phone
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_phone_book.*

class PhoneBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_book)

        setAdapter()
        btn_resetDB.setOnClickListener {
            getPhoneBook()
            setAdapter()
            Toast.makeText(applicationContext,"전화번호부 불러오기 끝",Toast.LENGTH_SHORT).show()
        }

    }

    fun setAdapter(){
        val helper = DBHelper(this)
        val adapter = PhoneBookAdapter()
        adapter.listData.addAll(helper.getPhoneBookLIST())

        phonebook_recyclerview.adapter = adapter
        phonebook_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    fun getPhoneBook(){
        val dbHelper: DBHelper = DBHelper(this)
        dbHelper.deletePhoneList()

        val c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        while(c!!.moveToNext()){
            val contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            dbHelper.insertPhoneLIST(contactName,phNumber)
        }
        c.close()
    }
}