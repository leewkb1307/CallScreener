package com.gmail.leewkb1307.callscreener

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.CallScreeningService
import android.util.Log
import androidx.preference.PreferenceManager


class CallScreenerService : CallScreeningService() {
    private val mLogTAG = "CallScreener"

    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection == DIRECTION_INCOMING) {
            val handle: Uri? = callDetails.handle

            val incomingNumber: String? =
                handle?.toString()?.replace("tel:", "")?.replace("%2B", "+")

            if (incomingNumber == null) {
                Log.d(mLogTAG, "Incoming number is NULL!")
            }
            else {
                Log.d(mLogTAG, "Incoming number --> $incomingNumber")
            }

            val isUnknownCall = incomingNumber == null || incomingNumber.isEmpty()
            
            var isEndCall = false
            val context: Context = applicationContext
            val sharedPref: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val prefMode = sharedPref.getString("prefMode", "allow_all")

            if (prefMode == "block_all") {
                isEndCall = true
            } else if (prefMode == "block_unknown") {
                isEndCall = isUnknownCall
            }

            val callResponseBuilder: CallResponse.Builder = CallResponse.Builder()

            if (isEndCall) {
                callResponseBuilder.apply {
                    setDisallowCall(true)
                    setRejectCall(true)
                }
            }

            respondToCall(callDetails,
                callResponseBuilder.build())
        }
    }
}
