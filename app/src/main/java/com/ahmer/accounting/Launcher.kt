package com.ahmer.accounting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.ui.Splash
import com.guardanis.applock.activities.UnlockActivity
import io.ahmer.utils.utilcode.SPUtils

class Launcher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themePref = SPUtils.getInstance(Constants.PREFERENCE_THEME)
        val isCheckedTheme = themePref.getBoolean(Constants.PREFERENCE_THEME_KEY)
        if (isCheckedTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val isLockActivated: SPUtils = SPUtils.getInstance(Constants.PREFERENCE_LOCKSCREEN)
        val isCheckedLock = isLockActivated.getBoolean(Constants.PREFERENCE_LOCKSCREEN_KEY)

        val activityResultLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val intent = Intent(applicationContext, Splash::class.java).apply {
                    putExtra(UnlockActivity.INTENT_ALLOW_UNLOCKED_EXIT, false)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                startActivity(intent)
                finish()
            }
        Log.v(Constants.LOG_TAG, "LockScreenKey: $isCheckedLock")

        if (isCheckedLock) {
            val intent = Intent(applicationContext, UnlockActivity::class.java)
            activityResultLauncher.launch(intent)
        } else {
            val intent = Intent(applicationContext, Splash::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            startActivity(intent)
            finish()
        }
    }
}
