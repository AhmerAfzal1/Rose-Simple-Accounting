package com.ahmer.accounting.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
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

        fun checkPermission(activity: Activity): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
                val allPermissions = arrayOf(writePermission, readPermission)
                return if (ContextCompat.checkSelfPermission(
                        activity,
                        writePermission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        allPermissions,
                        Constants.PERMISSION_REQUEST_CODE
                    )
                    false
                } else {
                    true
                }
            } else {
                return true
            }
        }

        private fun getAbsolutePath(file: File): String {
            return file.absolutePath
        }

        fun getInternalAppDatabasePath(context: Context): String {
            return getAbsolutePath(context.getDatabasePath(Constants.DATABASE_NAME))
        }
    }
}