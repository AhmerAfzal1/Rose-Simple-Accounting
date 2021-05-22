package com.ahmer.accounting.helper

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.Utils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class HelperFunctions {

    companion object {

        @JvmStatic
        fun getDateTime(format: String = "", isLocaleDefault: Boolean = true): String {
            val locale: Locale = if (isLocaleDefault) {
                Locale.getDefault()
            } else {
                Locale.ENGLISH
            }
            val dateFormat: SimpleDateFormat = if (format.isEmpty()) {
                SimpleDateFormat(Constants.DATE_TIME_PATTERN, locale)
            } else {
                SimpleDateFormat(format, locale)
            }
            val dateTime = Calendar.getInstance().time
            return dateFormat.format(dateTime)
        }

        @JvmStatic
        fun convertDateTimeShortFormat(dateTime: String, forStatement: Boolean = false): String {
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
                    val parsed = LocalDateTime.parse(dateTime, inputFormatter)

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
                    val date: Date = sdfOld.parse(dateTime)!!
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

        @JvmStatic
        fun getRoundedValue(value: String): String {
            val newValue = value.toDouble()
            val round = DecimalFormat("#,##0.##")
            round.roundingMode = RoundingMode.HALF_UP
            return round.format(newValue)
        }

        @JvmStatic
        fun checkBoolean(int: Int): Boolean {
            // 0 for false and 1 for true
            return int != 0
        }

        @JvmStatic
        fun convertColorIntToHexString(int: Int): String {
            return "#${Integer.toHexString(ContextCompat.getColor(Utils.getApp(), int))}"
        }

        @JvmStatic
        fun dateTimePickerShow(view: View): String {
            var dateTime = ""
            val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
            val currentDate = Calendar.getInstance()
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                currentDate.set(year, month, dayOfMonth)
                dateTime = dateFormat.format(currentDate.time)
            }
            val datePicker = DatePickerDialog(
                view.context,
                listener,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
            return dateTime
        }
    }
}