package com.gmail.leewkb1307.callscreener

import android.content.Context
import android.content.SharedPreferences
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.CallScreeningService
import androidx.preference.PreferenceManager


class CallScreenerService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection == DIRECTION_INCOMING) {
            var isEndCall = false
            val context: Context = applicationContext
            val sharedPref: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val prefMode = sharedPref.getString("prefMode", "allow_all")

            if (prefMode == "block_all") {
                isEndCall = true
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
