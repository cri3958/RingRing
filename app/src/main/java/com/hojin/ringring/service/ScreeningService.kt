package com.hojin.ringring.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.widget.Toast
import androidx.annotation.RequiresApi

class ScreeningService : CallScreeningService() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails?.callDirection == Call.Details.DIRECTION_INCOMING) {
            val phoneNumber = callDetails.handle.schemeSpecificPart
            Toast.makeText(this, "incoming number : " + phoneNumber, Toast.LENGTH_SHORT).show()
            //이거면되는건가?????
        }
    }
}
