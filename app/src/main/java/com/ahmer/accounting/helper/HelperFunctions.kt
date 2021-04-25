package com.ahmer.accounting.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class HelperFunctions : AppCompatActivity() {

    companion object {

        fun getDateTime(): String {
            val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
            val dateTime = Calendar.getInstance().time
            Log.v(Constants.LOG_TAG, dateFormat.format(dateTime))
            return dateFormat.format(dateTime)
        }

        fun makeToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        fun getRoundedValue(value: Double): Double {
            return BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        }
    }
}