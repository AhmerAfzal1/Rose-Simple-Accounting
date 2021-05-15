package com.ahmer.accounting

import android.app.backup.*
import android.os.ParcelFileDescriptor
import android.util.Log
import com.ahmer.accounting.helper.Constants
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.File
import java.io.IOException

class BackupAgent : BackupAgentHelper() {

    override fun onCreate() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val file: File = getDatabasePath(Constants.DATABASE_NAME)
        FileBackupHelper(this, file.name).also {
            addHelper("RoseDatabase", it)
        }
        //Add multiple SharedPreferences files name separate with comma
        SharedPreferencesBackupHelper(this, Constants.PREFERENCE_THEME).also {
            addHelper("RosePreferences", it)
        }
        val backupManager = BackupManager(this)
        backupManager.dataChanged()
    }

    override fun getFilesDir(): File {
        val file: File = getDatabasePath(Constants.DATABASE_NAME).parentFile!!
        Log.v(Constants.LOG_TAG, file.toString())
        return file
    }

    @Throws(IOException::class)
    override fun onBackup(
        oldState: ParcelFileDescriptor?,
        data: BackupDataOutput?,
        newState: ParcelFileDescriptor?
    ) {
        super.onBackup(oldState, data, newState)
    }

    @Throws(IOException::class)
    override fun onRestore(
        data: BackupDataInput?,
        appVersionCode: Int,
        newState: ParcelFileDescriptor?
    ) {
        super.onRestore(data, appVersionCode, newState)
    }
}