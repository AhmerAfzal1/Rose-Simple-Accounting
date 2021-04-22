package com.ahmer.accounting.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HelperFunctions : AppCompatActivity() {

    companion object {

        fun getDateTime(): String {
            val dateTimePattern = "dd MMM yyyy hh:mm:ss aa"
            val dateTimeFormat: DateFormat = SimpleDateFormat(dateTimePattern, Locale.UK)
            val dateTime = Calendar.getInstance().time
            Log.v(Constants.LOG_TAG, dateTimeFormat.format(dateTime))
            return dateTimeFormat.format(dateTime)
        }

        fun makeToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}