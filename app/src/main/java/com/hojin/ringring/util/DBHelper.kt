package com.hojin.ringring.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hojin.ringring.model.Phone
import java.text.SimpleDateFormat
import java.util.*


class DBHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private val DB_VERSION = 4
        private val DB_NAME = "RingRing.db"

        private val PHONELIST = "Phone"
        private val PHONE_ID = "Id"
        private val PHONE_NAME = "Name"
        private val PHONE_NUMBER = "Number"
        private val PHONE_ISUSING = "IsUsing"

        private val TIMER = "Timer"
        private val RESTARTTIME = "Restarttime"

        private val SERVICE = "Service"
        private val ISRUNNING = "IsRunning"

        private val LATESTCALL = "LatestCall"
        private val LATESTCALL_NUMBER = "Number"
        private val LATESTCALL_TIME = "Time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_PHONE =
            ("CREATE TABLE " + PHONELIST + "("
                    + PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PHONE_NAME + " TEXT,"
                    + PHONE_NUMBER + " TEXT,"
                    + PHONE_ISUSING + " TEXT)")

        val CREATE_TABLE_TIMER =
            ("CREATE TABLE "+TIMER + "("
                    + RESTARTTIME +" Text)")

        val CREATE_TABLE_SERVICE =
            ("CREATE TABLE "+SERVICE + "("
                    + ISRUNNING +" Text)")

        val CREATE_TABLE_LATESTCALL =
            ("CREATE TABLE "+LATESTCALL + "("
                    + LATESTCALL_NUMBER +" Text,"
                    + LATESTCALL_TIME +" Text)")

        db!!.execSQL(CREATE_TABLE_PHONE)
        db!!.execSQL(CREATE_TABLE_TIMER)
        db!!.execSQL(CREATE_TABLE_SERVICE)
        db!!.execSQL(CREATE_TABLE_LATESTCALL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newViersion: Int) {
        when (oldVersion) {
            1 -> {
                db!!.execSQL("DROP TABLE IF EXISTS $PHONELIST")
            }
            2->{
                db!!.execSQL("DROP TABLE IF EXISTS $TIMER")
            }
            3->{
                db!!.execSQL("DROP TABLE IF EXISTS $SERVICE")
            }
            4->{
                db!!.execSQL("DROP TABLE IF EXISTS $LATESTCALL")
            }
        }
        onCreate(db)
    }

    fun insertPhoneLIST(id:Int,name:String, number:String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PHONE_ID,id)
        contentValues.put(PHONE_NAME, name)
        contentValues.put(PHONE_NUMBER, number)
        contentValues.put(PHONE_ISUSING, "true")
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
        db.delete(PHONELIST, "$PHONE_NAME = ? ", arrayOf(phName))
        db.close()
    }

    fun isKnownNumber(phNumber1:String):Boolean{
        val db = this.readableDatabase
        var isKnownNumber = false

        val cursor = db.rawQuery("SELECT * FROM $PHONELIST",null)
        while (cursor.moveToNext()){
           if(phNumber1 == cursor.getString(cursor.getColumnIndex(PHONE_NUMBER))){
               Log.d("Matching PhoneNumber with ",cursor.getString(cursor.getColumnIndex(PHONE_NUMBER))+" / "+phNumber1)
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
        val cursor = db.rawQuery("SELECT * FROM $PHONELIST",null)
        while(cursor.moveToNext()){
            phone = Phone()
            phone.setId(num)
            phone.setName(cursor.getString(cursor.getColumnIndex(PHONE_NAME)))
            phone.setNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)))
            phone.setUsing(cursor.getString(cursor.getColumnIndex(PHONE_ISUSING)))
            phonebooklist.add(phone)
            num++
        }
        cursor.close()
        db.close()
        return phonebooklist
    }

    fun getCountBookList():Int{
        val db =this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PHONELIST",null)
        return cursor.count
    }

    fun updateUsingPhoneItem(phone: Phone){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PHONE_ID,phone.getId())
        contentValues.put(PHONE_NAME,phone.getName())
        contentValues.put(PHONE_NUMBER,phone.getNumber())
        contentValues.put(PHONE_ISUSING, phone.getUsing())

        db.update(PHONELIST,contentValues, PHONE_ID+" = "+phone.getId(),null)

        db.close()
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

    fun isWaitingService():Boolean{ //time1>time2이면, time1은 time2보다 미래이다.
        val db = this.readableDatabase
        val temptime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val now = dateFormat.format(Date(temptime))

        val cursor = db.rawQuery("SELECT * FROM $TIMER",null)
        while(cursor.moveToNext()) {
            if (now < cursor.getString(cursor.getColumnIndex(RESTARTTIME))) {//지금 시간이, 서비스 재시작 시간보다 과거인가?
                Log.d("@@@", now)
                Log.d("@@@", cursor.getString(cursor.getColumnIndex(RESTARTTIME)))
                Log.d("isWaitingService","return false")
                db.close()
                return true//지금시간이 서비스 재시작 시간보다 과거이다.
            }
        }
        db.close()
        Log.d("isWaitingService","return true")
        return false//지금시간이 서비스 재시작 시간보다 미래이다.
    }

    fun getTimer():String{
        val db =  this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TIMER",null)

        while(cursor.moveToNext()) {
            val time = cursor.getString(cursor.getColumnIndex(RESTARTTIME))
            if (time != null) {
                db.close()
                return time
            }
        }
        db.close()
        return "error"
    }

    fun SettingStatus(status : String){
        val db = this.writableDatabase

        db.delete(SERVICE,null,null)

        val contentValues = ContentValues()
        contentValues.put(ISRUNNING, status)
        db.insert(SERVICE, null, contentValues)
        db.close()
    }

    fun getStatus():String{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $SERVICE",null)

        while (cursor.moveToNext()) {
            val isRunning = cursor.getString(cursor.getColumnIndex(ISRUNNING))
            if (isRunning == "ON" || isRunning == "OFF") {
                db.close()
                return isRunning
            }
        }
        db.close()
        return "error"  //db에 status가 저장이 안되어있었다면? == 어플 처음 실행할때
    }

    fun SettingLatestCall(number:String){
        val db = this.writableDatabase

        db.delete(LATESTCALL,null,null)

        val temptime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val now = dateFormat.format(Date(temptime))

        val contentValues = ContentValues()
        contentValues.put(LATESTCALL_NUMBER,number)
        contentValues.put(LATESTCALL_TIME,now)
        db.insert(LATESTCALL,null,contentValues)
        db.close()
        Log.d("SettingLatestCall",number+" / "+now)
    }

    fun compareLatestCall(number:String):Boolean{   //테스트 해봐야됨
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROm $LATESTCALL",null)
        while(cursor.moveToNext()) {
            val temptime = System.currentTimeMillis() - 300000 //지금보다 5분전
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val five_minutes_ago = dateFormat.format(Date(temptime))

            val LatestCall_number = cursor.getString(cursor.getColumnIndex(LATESTCALL_NUMBER))
            val LatestCall_time = cursor.getString(cursor.getColumnIndex(LATESTCALL_TIME))

            Log.d("@@@@", LatestCall_number)
            Log.d("@@@@", number)
            Log.d("@@@@", five_minutes_ago)
            Log.d("@@@@", LatestCall_time)
            if (LatestCall_number == number && five_minutes_ago < LatestCall_time) {//5분내로 같은 번호로 전화가 온적이 있다.
                db.close()
                return true
            }
        }
        db.close()
        return false
    }
}