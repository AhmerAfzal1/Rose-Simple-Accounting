package com.ahmer.accounting.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import io.ahmer.utils.constants.PermissionConstants
import io.ahmer.utils.utilcode.PermissionUtils
import io.ahmer.utils.utilcode.SPUtils
import io.ahmer.utils.utilcode.ScreenUtils

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        val themePref = SPUtils.getInstance(Constants.PREFERENCE_THEME)
        val isChecked = themePref.getBoolean(Constants.PREFERENCE_THEME_KEY)
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        checkPermission(this)
    }

    private fun checkPermission(context: Context) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .rationale { activity, shouldRequest ->
                Log.v(Constants.LOG_TAG, "Again permission checking")
                shouldRequest.again(true)
            }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    Log.v(Constants.LOG_TAG, "Permission has been granted")
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>
                ) {
                    Log.v(Constants.LOG_TAG, "Permission has not been granted")
                    if (denied.isNotEmpty() || deniedForever.isNotEmpty()) {
                        finish()
                    }
                }
            })
            .theme { activity ->
                ScreenUtils.setFullScreen(activity)
            }.request()
    }
}