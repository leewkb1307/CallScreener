package com.gmail.leewkb1307.callscreener

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
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

            val isUnknownCall = incomingNumber?.isEmpty() ?: true

            val context: Context = applicationContext
            val sharedPref: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val prefMode = sharedPref.getString("prefMode", "allow_all")

            val isEndCall = when (prefMode) {
                "block_all" -> {
                    true
                }
                "block_unknown" -> {
                    isUnknownCall
                }
                "allow_contact" -> {
                    (incomingNumber == null) || !isInContact(context, incomingNumber)
                }
                else -> {
                    false
                }
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

    private fun isInContact(context: Context, phoneNumber: String): Boolean {
        val isContact: Boolean

        val resolver: ContentResolver = context.contentResolver
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(
                phoneNumber
            )
        )
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)

        isContact = (cursor?.count ?: 0) > 0
        cursor?.close()

        return isContact
    }
}
