package com.hojin.ringring.model

class Phone {

    private var phName:String? = null
    private var phNumber:String? = null
    private var id = 0

    fun Phone(){}
    fun Phone(Name:String,Number:String){
        this.phName = Name
        this.phNumber = Number
    }

    fun getId():Int { return id}
    fun setId(id: Int){this.id =id}

    fun getName(): String? { return phName}
    fun setName(text:String?){this.phName=text}

    fun getNumber(): String? { return phNumber}
    fun setNumber(text:String?){this.phNumber=text}
}