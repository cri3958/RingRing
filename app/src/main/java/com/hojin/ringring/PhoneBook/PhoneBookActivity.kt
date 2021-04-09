package com.hojin.ringring.PhoneBook

import android.content.DialogInterface
import android.os.Bundle
import android.provider.ContactsContract
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
import kotlinx.android.synthetic.main.item_phonebooklist.view.*


class PhoneBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_book)

        setAdapter()

        btn_resetDB.setOnClickListener {
            callPhoneBook()
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

        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {//4 : 왼쪽으로
                var builder = AlertDialog.Builder(applicationContext)
                builder.setTitle("전화번호부를 삭제")
                builder.setMessage("삭제하시겠습니까?")
                var listner = object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when(p1){
                            DialogInterface.BUTTON_POSITIVE -> {
                                helper.deletePhoneItem(viewHolder.itemView.phone_name.text.toString())  //대충 여기만드는중
                            }

                        }
                    }

                }
                builder.setPositiveButton("네",)
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(phonebook_recyclerview)
    }

    fun callPhoneBook(){
        val dbHelper: DBHelper = DBHelper(this)
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
}