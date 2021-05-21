package com.ahmer.accounting.helper

import android.os.Build
import android.util.Log
import androidx.databinding.BindingConversion
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
        fun setEmptyString(): String {
            return ""
        }
    }
}