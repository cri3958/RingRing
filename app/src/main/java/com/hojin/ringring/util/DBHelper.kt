package com.hojin.ringring.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hojin.ringring.model.Phone


class DBHelper (context : Context) : SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
) {

    companion object{
        private val DB_VERSION = 1
        private val DB_NAME = "RingRing.db"

        private val PHONELIST = "phone"

        private val PHONE_ID = "id"
        private val PHONE_NAME = "name"
        private val PHONE_NUMBER = "number"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =
            ("CREATE TABLE " + PHONELIST + "("
                    + PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PHONE_NAME + " TEXT,"
                    + PHONE_NUMBER + " TEXT"+ ")")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newViersion: Int) {
        when (oldVersion) {
            1 -> {
                db!!.execSQL("DROP TABLE IF EXISTS $PHONELIST")
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

    fun deletePhoneList() { //sql지우기
        val db = this.writableDatabase
        db.delete(PHONELIST, null,null)
        db.close()
    }

    fun isKnownNumber(phNumber1:String):Boolean{
        val db = this.writableDatabase //readable로 바꾸어도 되는가???
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
        val cursor = db.rawQuery("SELECT * FROM $PHONELIST ORDER BY $PHONE_ID",null)
        while(cursor.moveToNext()){
            phone = Phone()
            phone.setId(cursor.getInt(cursor.getColumnIndex(PHONE_ID)))
            phone.setName(cursor.getString(cursor.getColumnIndex(PHONE_NAME)))
            phone.setNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)))
            phonebooklist.add(phone)
        }
        cursor.close()
        db.close()
        return phonebooklist
    }
}