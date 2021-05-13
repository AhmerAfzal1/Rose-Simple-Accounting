package com.ahmer.accounting

import android.app.Application
import io.ahmer.utils.utilcode.Utils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}