package com.ahmer.accounting.helper

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.Utils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

object HelperFunctions {

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
    fun getRoundedValue(value: Double): String {
        val round = DecimalFormat("#,##0.##")
        round.roundingMode = RoundingMode.HALF_UP
        return round.format(value)
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
}