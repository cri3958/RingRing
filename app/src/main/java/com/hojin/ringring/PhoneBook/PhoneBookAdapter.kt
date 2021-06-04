package com.hojin.ringring.PhoneBook

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hojin.ringring.R
import com.hojin.ringring.model.Phone
import com.hojin.ringring.util.DBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_phonebooklist.view.*


class PhoneBookAdapter : RecyclerView. Adapter<RecyclerView.ViewHolder>(){
    var mcontext: Context? = null
    var listData = ArrayList<Phone>()

    fun setContext(context: Context){
        this.mcontext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phonebooklist,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dbHelper = DBHelper(mcontext)

        val phone = listData[position]
        holder.itemView.phone_dbnum.text = (position+1).toString()
        holder.itemView.phone_name.text = phone.getName()
        holder.itemView.phone_number.text = phone.getNumber()
        if(phone.getUsing() == "true")
            holder.itemView.phone_switch.isChecked = true
        holder.itemView.phone_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {//ON
                phone.setUsing("true")
                dbHelper.updateUsingPhoneItem(phone)
            } else {//OFF
                phone.setUsing("false")
                dbHelper.updateUsingPhoneItem(phone)
            }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){}
}