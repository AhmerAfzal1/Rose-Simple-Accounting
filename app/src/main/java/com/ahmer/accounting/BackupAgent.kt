package com.ahmer.accounting

import android.app.backup.*
import android.os.ParcelFileDescriptor
import com.ahmer.accounting.Constants.Companion.DATABASE_NAME
import java.io.File
import java.io.IOException

class BackupAgent : BackupAgentHelper() {

    override fun onCreate() {
        val file = getDatabasePath(DATABASE_NAME)
        val database = FileBackupHelper(this, file.name)
        addHelper(DATABASE_NAME, database)
        val backupManager = BackupManager(this)
        backupManager.dataChanged();
    }

    override fun getFilesDir(): File {
        return getDatabasePath(DATABASE_NAME).parentFile!!
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