package com.ahmer.accounting.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.SplashBinding
import com.ahmer.accounting.helper.Constants
import io.ahmer.utils.constants.PermissionConstants
import io.ahmer.utils.utilcode.PermissionUtils
import io.ahmer.utils.utilcode.ScreenUtils

class Splash : AppCompatActivity() {

    private lateinit var mBinding: SplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.splash)
        checkPermission(this)
    }

    private fun checkPermission(mActivity: Activity) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .rationale { activity, shouldRequest ->
                Log.v(Constants.LOG_TAG, "Again permission checking")
                shouldRequest.again(true)
            }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    Log.v(Constants.LOG_TAG, "Permission has been granted")
                    val intent = Intent(mActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                    mActivity.startActivity(intent)
                    mActivity.finish()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>
                ) {
                    Log.v(Constants.LOG_TAG, "Permission has not been granted")
                    if (denied.isNotEmpty() || deniedForever.isNotEmpty()) {
                        mActivity.finish()
                    }
                }
            })
            .theme { activity ->
                ScreenUtils.setFullScreen(activity)
            }.request()
    }
}