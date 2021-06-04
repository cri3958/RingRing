package com.hojin.ringring.util

class util {
    fun formatNumber(number: String): String {  //https://ko.wikipedia.org/wiki/%EB%8C%80%ED%95%9C%EB%AF%BC%EA%B5%AD%EC%9D%98_%EC%A0%84%ED%99%94%EB%B2%88%ED%98%B8_%EC%B2%B4%EA%B3%84
        var returnstr:String = "error"
        val numberarray = number.split("")

        if(numberarray[3]=="-" || numberarray[4]=="-")
            return number

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