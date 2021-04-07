package com.hojin.ringring.PhoneBook

import android.content.Context
import android.system.Os.bind
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hojin.ringring.R
import com.hojin.ringring.model.Phone
import kotlinx.android.synthetic.main.item_phonebooklist.view.*

class PhoneBookAdapter : RecyclerView. Adapter<RecyclerView.ViewHolder>(){
    var listData = mutableListOf<Phone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phonebooklist,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val phone = listData.get(position)
        holder.itemView.phone_name.text = phone.getName()
        holder.itemView.phone_number.text = phone.getNumber()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun setMemo(phone:Phone){
            itemView.phone_name.text = phone.getName()
            itemView.phone_number.text = phone.getNumber()
        }
    }
}