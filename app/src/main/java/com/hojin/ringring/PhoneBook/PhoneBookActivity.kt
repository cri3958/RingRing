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
import com.hojin.ringring.util.util
import kotlinx.android.synthetic.main.activity_phone_book.*
import kotlinx.android.synthetic.main.floating_dialog.view.*
import kotlinx.android.synthetic.main.item_phonebooklist.view.*


class PhoneBookActivity : AppCompatActivity() { //https://sbe03005dev.tistory.com/entry/Android-Kotlin-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%BD%94%ED%8B%80%EB%A6%B0-%EB%8B%A4%EC%9D%B4%EC%96%BC%EB%A1%9C%EA%B7%B8-Dialog
    val context:Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_book)

        val dbHelper = DBHelper(context)
        val util = util()

        setAdapter(context)

        phone_countbook.text = dbHelper.getCountBookList().toString()

        btn_back.setOnClickListener {
            finish()
        }

        btn_resetDB.setOnClickListener {
            callPhoneBook()
            setAdapter(context)
            phone_countbook.text = dbHelper.getCountBookList().toString()
            Toast.makeText(applicationContext,"전화번호부 불러오기 끝",Toast.LENGTH_SHORT).show()
        }

        floatingbtn_addphonelist.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("전화번호부 추가하기")

            val v1 = layoutInflater.inflate(R.layout.floating_dialog,null)
            builder.setView(v1)
            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1){
                    DialogInterface.BUTTON_POSITIVE -> {
                        val newname : String = v1.dialog_name.text.toString()
                        val newnumber : String = v1.dialog_number.text.toString()
                        val changenumber = util.formatNumber(newnumber)

                        Log.d("@@@@",newname+" / "+changenumber)
                        dbHelper.insertPhoneLIST(dbHelper.getCountBookList()+1,newname,changenumber)

                        setAdapter(context)
                        phone_countbook.text = dbHelper.getCountBookList().toString()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> null
                }
            }
            builder.setPositiveButton("추가하기",listener)
            builder.setNegativeButton("취소",listener)
            builder.show()
        }

        btn_itemdelete.setOnClickListener {
            
        }
    }

    fun setAdapter(context:Context){
        val helper = DBHelper(context)
        val adapter = PhoneBookAdapter()

        adapter.setContext(context)
        adapter.listData.addAll(helper.getPhoneBookLIST())

        phonebook_recyclerview.adapter = adapter
        phonebook_recyclerview.layoutManager = LinearLayoutManager(context)

        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {//4 : 왼쪽으로
                helper.deletePhoneItem(viewHolder.itemView.phone_name.text.toString())
                val adapter1 = PhoneBookAdapter()
                adapter1.listData.addAll(helper.getPhoneBookLIST())
                phonebook_recyclerview.adapter = adapter1
                phone_countbook.text = helper.getCountBookList().toString()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(phonebook_recyclerview)
    }

    fun callPhoneBook(){
        val dbHelper: DBHelper = DBHelper(context)
        dbHelper.deletePhoneList()

        var id = 1
        val c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        while(c!!.moveToNext()){

            val contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            if(!dbHelper.isKnownNumber(phNumber)){
                dbHelper.insertPhoneLIST(id++,contactName,phNumber)
            }
        }
        c.close()
    }
}