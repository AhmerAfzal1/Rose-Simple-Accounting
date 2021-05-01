package com.ahmer.accounting.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.afzal.utils.constants.PermissionConstants
import com.ahmer.afzal.utils.utilcode.PermissionUtils
import com.ahmer.afzal.utils.utilcode.ScreenUtils
import java.math.RoundingMode
import java.text.DecimalFormat
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

        fun getRoundedValue(value: Double): String {
            val round = DecimalFormat("#,##0.##")
            round.roundingMode = RoundingMode.HALF_UP
            return round.format(value)
        }

        fun checkPermission() {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale { activity, shouldRequest ->
                    shouldRequest.again(true)
                }.theme {
                    PermissionUtils.ThemeCallback { activity ->
                        ScreenUtils.setFullScreen(activity)
                    }
                }.request()
        }
    }
}