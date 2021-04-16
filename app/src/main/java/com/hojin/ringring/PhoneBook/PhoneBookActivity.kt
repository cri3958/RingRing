package com.hojin.ringring.PhoneBook

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hojin.ringring.R
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_phone_book.*
import kotlinx.android.synthetic.main.floating_dialog.*
import kotlinx.android.synthetic.main.floating_dialog.view.*
import kotlinx.android.synthetic.main.item_phonebooklist.view.*


class PhoneBookActivity : AppCompatActivity() { //https://sbe03005dev.tistory.com/entry/Android-Kotlin-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%BD%94%ED%8B%80%EB%A6%B0-%EB%8B%A4%EC%9D%B4%EC%96%BC%EB%A1%9C%EA%B7%B8-Dialog
    val context:Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_book)

        val dbHelper = DBHelper(context)

        setAdapter(context)

        btn_resetDB.setOnClickListener {
            callPhoneBook()
            setAdapter(context)
            Toast.makeText(applicationContext,"전화번호부 불러오기 끝",Toast.LENGTH_SHORT).show()
        }

        floatingbtn_addphonelist.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("전화번호부 추가하기")

            var v1 = layoutInflater.inflate(R.layout.floating_dialog,null)
            builder.setView(v1)
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when(p1){
                        DialogInterface.BUTTON_POSITIVE -> {
                            val newname : String = v1.dialog_name.text.toString()
                            val newnumber : String = v1.dialog_number.text.toString()
                            val changenumber = formatNumber(newnumber)

                            Log.d("@@@@",newname+" / "+changenumber)
                            dbHelper.insertPhoneLIST(newname,changenumber)

                            setAdapter(context)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> null
                    }
                }
            }
            builder.setPositiveButton("추가하기",listener)
            builder.setNegativeButton("취소",listener)
            builder.show()
        }

    }

    fun setAdapter(context:Context){
        val helper = DBHelper(context)
        val adapter = PhoneBookAdapter()
        adapter.listData.addAll(helper.getPhoneBookLIST())

        phonebook_recyclerview.adapter = adapter
        phonebook_recyclerview.layoutManager = LinearLayoutManager(context)

        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {//4 : 왼쪽으로
                var builder = AlertDialog.Builder(context)
                builder.setTitle("전화번호부를 삭제")
                builder.setMessage("삭제하시겠습니까?")
                var listner = object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when(p1){
                            DialogInterface.BUTTON_POSITIVE -> {
                                helper.deletePhoneItem(viewHolder.itemView.phone_name.text.toString())  //대충 여기만드는중
                                val adapter1 = PhoneBookAdapter()
                                adapter1.listData.addAll(helper.getPhoneBookLIST())

                                phonebook_recyclerview.adapter = adapter1
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                phonebook_recyclerview.adapter = adapter
                            }
                        }
                    }
                }
                builder.setPositiveButton("네",listner)
                builder.setNegativeButton("아니오",listner)
                builder.show()
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(phonebook_recyclerview)
    }

    fun callPhoneBook(){
        val dbHelper: DBHelper = DBHelper(context)
        dbHelper.deletePhoneList()

        val c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        while(c!!.moveToNext()){
            val contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            if(!dbHelper.isKnownNumber(phNumber)){
                dbHelper.insertPhoneLIST(contactName,phNumber)
            }
        }
        c.close()
    }

    fun formatNumber(number: String): String {  //https://ko.wikipedia.org/wiki/%EB%8C%80%ED%95%9C%EB%AF%BC%EA%B5%AD%EC%9D%98_%EC%A0%84%ED%99%94%EB%B2%88%ED%98%B8_%EC%B2%B4%EA%B3%84
        //앞에가 010,011,016~~~이면 하나, 02,031,032~~~이면 하나 ~~~~ >> 이외는 번호취급 안함... 이정도면 잡겠지
        var returnstr:String = "error"
        val numberarray = number.split("")

        if(number.length == 11) {   //01X-XXXX-XXXX || 031~064-XXXX-XXXX
            returnstr = numberarray[1]+numberarray[2]+numberarray[3]+"-"+numberarray[4]+numberarray[5]+numberarray[6]+numberarray[7]+"-"+numberarray[8]+numberarray[9]+numberarray[10]+numberarray[11]
        }else if(number.length == 10){  //02-XXXX-XXXX || 031~064-XXX-XXXX
            if(numberarray[2].equals("2")){
                returnstr = numberarray[1]+numberarray[2]+"-"+numberarray[3]+numberarray[4]+numberarray[5]+numberarray[6]+"-"+numberarray[7]+numberarray[8]+numberarray[9]+numberarray[10]
            }else{
                returnstr = numberarray[1]+numberarray[2]+numberarray[3]+"-"+numberarray[4]+numberarray[5]+numberarray[6]+"-"+numberarray[7]+numberarray[8]+numberarray[9]+numberarray[10]
            }
        } else if(number.length == 9){  //02-XXX-XXXX
            returnstr = numberarray[1]+numberarray[2]+"-"+numberarray[3]+numberarray[4]+numberarray[5]+"-"+numberarray[6]+numberarray[7]+numberarray[8]+numberarray[9]
        }
        return returnstr
    }
}