package com.ahmer.accounting

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import io.ahmer.utils.utilcode.Utils

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        Utils.init(this)
    }
}