package com.gmail.leewkb1307.callscreener

import android.telecom.Call
import android.telecom.CallScreeningService

class CallScreenerService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        respondToCall(callDetails,
            CallResponse.Builder().setDisallowCall(true).setRejectCall(true).build())
    }
}
