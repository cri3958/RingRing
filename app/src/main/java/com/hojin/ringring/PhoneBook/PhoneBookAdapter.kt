package com.hojin.ringring.PhoneBook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        holder.itemView.phone_dbnum.text = phone.getId().toString()
        holder.itemView.phone_name.text = phone.getName()
        holder.itemView.phone_number.text = phone.getNumber()
        holder.itemView.phone_checkbox.setOnClickListener {
            if(holder.itemView.phone_checkbox.isChecked){
                //어딘가에저장
            }else{
                //저장해제
            }
        }//>>이걸 어떻게 저렇게 PhoneBookActivity로 보내야됨
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){}
}