package com.ahmer.accounting.helper

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

        fun confirmDelete(context: Context, mId: Long, mUserName: String, isUserDelete: Boolean) {
            try {
                var isDeleted = false
                val myDatabaseHelper = MyDatabaseHelper(context)
                val alertBuilder = MaterialAlertDialogBuilder(context)
                alertBuilder.setTitle("Confirmation")
                alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
                val msg: String = if (isUserDelete) {
                    context.getString(R.string.user_delete_warning_msg, mUserName)
                } else {
                    context.getString(R.string.trans_delete_warning_msg)
                }
                alertBuilder.setMessage(msg)
                alertBuilder.setCancelable(false)
                alertBuilder.setPositiveButton("Delete") { dialog, which ->
                    isDeleted = if (isUserDelete) {
                        myDatabaseHelper.deleteUserProfileData(mId)
                    } else {
                        myDatabaseHelper.deleteTransactions(mId)
                    }
                    dialog.dismiss()
                }
                alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = alertBuilder.create()
                dialog.show()
                dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
                if (isDeleted) {
                    if (isUserDelete) {
                        makeToast(context, context.getString(R.string.users_deleted, mUserName))
                    } else {
                        makeToast(context, context.getString(R.string.trans_deleted))
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}