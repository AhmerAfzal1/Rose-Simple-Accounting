package com.ahmer.accounting.helper

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class HelperFunctions : AppCompatActivity() {

    companion object {

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

        fun loadBannerAd(activity: Activity, adView: AdView) {
            MobileAds.initialize(activity)
            adView.adListener = object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.v(Constants.LOG_TAG, "Banner: Ad closed")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    Log.v(
                        Constants.LOG_TAG,
                        "Banner: Failed to load ad due to ${adError.message}, and error code is: ${adError.code}"
                    )
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    Log.v(Constants.LOG_TAG, "Banner: Ad opened and displayed")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.v(Constants.LOG_TAG, "Banner: Ad received and loading...")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.v(Constants.LOG_TAG, "Banner: User clicked on ad")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.v(Constants.LOG_TAG, "Banner: Ad impression is recorded")
                }
            }
        }
    }
}