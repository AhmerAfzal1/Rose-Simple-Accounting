package com.ahmer.accounting.helper

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ahmer.accounting.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class HelperFunctions : AppCompatActivity() {

    companion object {

        fun getDateTime(): String {
            val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
            val dateTime = Calendar.getInstance().time
            return dateFormat.format(dateTime)
        }

        fun getDateTimeForFileName(): String {
            var dateTime = ""
            try {
                val format = SimpleDateFormat("ddMMyyHHmmss", Locale.ENGLISH)
                dateTime = format.format(Date()).toString()
            } catch (pe: Exception) {
                Log.e(Constants.LOG_TAG, pe.message, pe)
                FirebaseCrashlytics.getInstance().recordException(pe)
            }
            return dateTime
        }

        fun convertDateTimeShortFormat(dataTime: String, forStatement: Boolean = false): String {
            var dateTimeShort = ""
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val inputFormatter = DateTimeFormatterBuilder().parseCaseInsensitive()
                        .append(
                            DateTimeFormatter.ofPattern(
                                Constants.DATE_TIME_PATTERN,
                                Locale.getDefault()
                            )
                        )
                        .toFormatter()
                    val parsed = LocalDateTime.parse(dataTime, inputFormatter)

                    val outputFormatter: DateTimeFormatter = if (!forStatement) {
                        DateTimeFormatter.ofPattern(
                            Constants.DATE_SHORT_PATTERN,
                            Locale.getDefault()
                        )
                    } else {
                        DateTimeFormatter.ofPattern(
                            Constants.DATE_LONG_PATTERN,
                            Locale.getDefault()
                        )
                    }
                    dateTimeShort = outputFormatter.format(parsed)
                } else {
                    val sdfOld = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
                    val date: Date = sdfOld.parse(dataTime)!!
                    val sdfNew: SimpleDateFormat = if (!forStatement) {
                        SimpleDateFormat(Constants.DATE_SHORT_PATTERN, Locale.getDefault())
                    } else {
                        SimpleDateFormat(Constants.DATE_LONG_PATTERN, Locale.getDefault())
                    }
                    dateTimeShort = sdfNew.format(date)
                }
            } catch (pe: Exception) {
                Log.e(Constants.LOG_TAG, pe.message, pe)
                FirebaseCrashlytics.getInstance().recordException(pe)
            }
            return dateTimeShort
        }

        fun getRoundedValue(value: Double): String {
            val round = DecimalFormat("#,##0.##")
            round.roundingMode = RoundingMode.HALF_UP
            return round.format(value)
        }

        fun checkBoolean(int: Int): Boolean {
            // 0 for false and 1 for true
            return int != 0
        }

        fun confirmDelete(context: Context, id: Long, userName: String = "", isUserDel: Boolean) {
            try {
                val myDatabaseHelper = MyDatabaseHelper(context)
                val alertBuilder = MaterialAlertDialogBuilder(context)
                alertBuilder.setTitle(context.getString(R.string.confirmation))
                alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
                val msg: String = if (isUserDel) {
                    context.getString(R.string.user_delete_warning_msg, userName)
                } else {
                    context.getString(R.string.trans_delete_warning_msg)
                }
                alertBuilder.setMessage(msg)
                alertBuilder.setCancelable(false)
                alertBuilder.setPositiveButton(context.getString(R.string.delete)) { dialog, which ->
                    val isDeleted: Boolean
                    if (isUserDel) {
                        isDeleted = myDatabaseHelper.deleteUserProfileData(id)
                        if (isDeleted) {
                            ToastUtils.showShort(
                                context.getString(R.string.users_deleted, userName)
                            )
                        }
                    } else {
                        isDeleted = myDatabaseHelper.deleteTransactions(id)
                        if (isDeleted) {
                            ToastUtils.showShort(context.getString(R.string.trans_deleted))
                        }
                    }
                    dialog.dismiss()
                }
                alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = alertBuilder.create()
                dialog.show()
                dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.getColor(R.color.black))
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.resources.getColor(R.color.black))
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}