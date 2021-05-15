package com.hojin.ringring.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.hojin.ringring.model.Phone
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class DBHelper (context : Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private val DB_VERSION = 2
        private val DB_NAME = "RingRing.db"

        private val PHONELIST = "phone"
        private val PHONE_ID = "id"
        private val PHONE_NAME = "name"
        private val PHONE_NUMBER = "number"

        private val TIMER = "timer"
        private val RESTARTTIME = "restarttime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_PHONE =
            ("CREATE TABLE " + PHONELIST + "("
                    + PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PHONE_NAME + " TEXT,"
                    + PHONE_NUMBER + " TEXT)")

        val CREATE_TABLE_TIMER =
            ("CREATE TABLE "+TIMER + "("
                    + RESTARTTIME +" Text)")
        db!!.execSQL(CREATE_TABLE_PHONE)
        db!!.execSQL(CREATE_TABLE_TIMER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newViersion: Int) {
        when (oldVersion) {
            1 -> {
                db!!.execSQL("DROP TABLE IF EXISTS $PHONELIST")
            }
            2->{
                db!!.execSQL("DROP TABLE IF EXISTS $TIMER")
            }
        }
        onCreate(db)
    }

    fun insertPhoneLIST(name:String,number:String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PHONE_NAME, name)
        contentValues.put(PHONE_NUMBER, number)
        db.insert(PHONELIST, null, contentValues)
        db.close()
    }

    fun deletePhoneList() {
        val db = this.writableDatabase
        db.delete(PHONELIST, null,null)
        db.close()
    }

    fun deletePhoneItem(phName:String){
        val db = this.writableDatabase
        db.delete(PHONELIST, PHONE_NAME+" = ? ", arrayOf(phName))
        db.close()
    }

    fun isKnownNumber(phNumber1:String):Boolean{
        val db = this.readableDatabase //readable로 바꾸어도 되는가???
        var isKnownNumber = false

        val cursor = db.rawQuery("SELECT * FROM $PHONELIST ORDER BY $PHONE_ID",null)
        while (cursor.moveToNext()){
            //Log.d("Matching PhoneNumber with ",cursor.getString(cursor.getColumnIndex(PHONE_NUMBER))+" / "+phNumber1)
           if(phNumber1.equals(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)))){
               isKnownNumber = true
               return isKnownNumber
           }
        }
        cursor.close()
        db.close()

        return isKnownNumber
    }

    fun getPhoneBookLIST(): MutableList<Phone> {
        val db = this.readableDatabase
        val phonebooklist = mutableListOf<Phone>()
        var phone:Phone
        var num = 1
        val cursor = db.rawQuery("SELECT * FROM $PHONELIST ORDER BY $PHONE_NAME",null)
        while(cursor.moveToNext()){
            phone = Phone()
            phone.setId(num)
            phone.setName(cursor.getString(cursor.getColumnIndex(PHONE_NAME)))
            phone.setNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)))
            phonebooklist.add(phone)
            num++
        }
        cursor.close()
        db.close()
        return phonebooklist
    }

    fun SettingTimer(time:Int){
        val db = this.writableDatabase

        db.delete(TIMER,null,null)

        val temptime = System.currentTimeMillis() + (time*60000)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val restarttime = dateFormat.format(Date(temptime))

        val contentValues = ContentValues()
        contentValues.put(RESTARTTIME, restarttime)
        db.insert(TIMER, null, contentValues)
        db.close()

    }

    fun CheckingTimer():Boolean{
        val db = this.readableDatabase
        val temptime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val restarttime = dateFormat.format(Date(temptime))

        val cursor = db.rawQuery("SELECT * FROM $TIMER",null)
        cursor.moveToFirst()
        if(restarttime>cursor.getString(cursor.getColumnIndex(RESTARTTIME))){//지금이 재시작 시간보다 후 일때(확인해봐야함)
            return true
        }
        return false
    }
}