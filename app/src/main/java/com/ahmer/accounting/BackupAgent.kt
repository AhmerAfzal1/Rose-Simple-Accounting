package com.ahmer.accounting

import android.app.backup.*
import android.os.ParcelFileDescriptor
import android.util.Log
import com.ahmer.accounting.helper.Constants.Companion.DATABASE_NAME
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import java.io.File
import java.io.IOException

class BackupAgent : BackupAgentHelper() {

    override fun onCreate() {
        val file = getDatabasePath(DATABASE_NAME)
        val database = FileBackupHelper(this, file.name)
        addHelper("SimpleAccounting", database)
        val backupManager = BackupManager(this)
        backupManager.dataChanged()
    }

    override fun getFilesDir(): File {
        val file: File = getDatabasePath(DATABASE_NAME).parentFile!!
        Log.v(LOG_TAG, file.toString())
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