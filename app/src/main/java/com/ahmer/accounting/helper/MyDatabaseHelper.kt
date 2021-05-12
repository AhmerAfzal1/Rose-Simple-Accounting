package com.ahmer.accounting.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.ahmer.accounting.R
import com.ahmer.accounting.model.Transactions
import com.ahmer.accounting.model.UserProfile
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.*


class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION) {

    private val mContext = context

    private fun cvUser(userProfile: UserProfile, isCreated: Boolean = true): ContentValues {
        return ContentValues().apply {
            put(Constants.UserColumn.NAME, userProfile.name)
            put(Constants.UserColumn.GENDER, userProfile.gender)
            put(Constants.UserColumn.ADDRESS, userProfile.address)
            put(Constants.UserColumn.CITY, userProfile.city)
            put(Constants.UserColumn.PHONE1, userProfile.phone1)
            put(Constants.UserColumn.PHONE2, userProfile.phone2)
            put(Constants.UserColumn.EMAIL, userProfile.email)
            put(Constants.UserColumn.COMMENTS, userProfile.comment)
            if (isCreated) {
                put(Constants.UserColumn.CREATED_ON, userProfile.created)
            } else {
                put(Constants.UserColumn.LAST_MODIFIED, userProfile.modified)
            }
        }
    }

    private fun cvTransaction(trans: Transactions, isModified: Boolean = false): ContentValues {
        return ContentValues().apply {
            put(Constants.TranColumn.USER_ID, trans.userId)
            put(Constants.TranColumn.DATE, trans.date)
            put(Constants.TranColumn.DESCRIPTION, trans.description)
            put(Constants.TranColumn.CREDIT, trans.credit)
            put(Constants.TranColumn.DEBIT, trans.debit)
            put(Constants.TranColumn.BALANCE, trans.balance)
            if (isModified) {
                put(Constants.TranColumn.LAST_MODIFIED, trans.modified)
            } else {
                put(Constants.TranColumn.CREATED_ON, trans.created)
            }
        }
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        try {
            val userTable = "CREATE TABLE IF NOT EXISTS ${Constants.UserColumn.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "${Constants.UserColumn.NAME} TEXT NOT NULL, " +
                    "${Constants.UserColumn.GENDER} VARCHAR(7) NOT NULL, " +
                    "${Constants.UserColumn.ADDRESS} TEXT, " +
                    "${Constants.UserColumn.CITY} TEXT, " +
                    "${Constants.UserColumn.PHONE1} TEXT, " +
                    "${Constants.UserColumn.PHONE2} TEXT, " +
                    "${Constants.UserColumn.EMAIL} TEXT, " +
                    "${Constants.UserColumn.COMMENTS} TEXT, " +
                    "${Constants.UserColumn.CREATED_ON} TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "${Constants.UserColumn.LAST_MODIFIED} TIMESTAMP DEFAULT \"\", " +
                    "CHECK(${Constants.UserColumn.GENDER} IN ('Male', 'Female', 'Unknown'))" +
                    ");"
            val tranTable = "CREATE TABLE IF NOT EXISTS ${Constants.TranColumn.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "${Constants.TranColumn.USER_ID} INTEGER, " +
                    "${Constants.TranColumn.DATE} DATE DEFAULT CURRENT_DATE NOT NULL, " +
                    "${Constants.TranColumn.DESCRIPTION} TEXT NOT NULL, " +
                    "${Constants.TranColumn.CREDIT} REAL, " +
                    "${Constants.TranColumn.DEBIT} REAL, " +
                    "${Constants.TranColumn.BALANCE} REAL, " +
                    "${Constants.TranColumn.CREATED_ON} TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "${Constants.TranColumn.LAST_MODIFIED} TIMESTAMP DEFAULT \"\", " +
                    "FOREIGN KEY (${Constants.TranColumn.USER_ID}) REFERENCES ${Constants.UserColumn.TABLE_NAME}(${BaseColumns._ID}) ON DELETE CASCADE" +
                    ");"
            db?.execSQL("PRAGMA foreign_keys = ON;")
            db?.execSQL(userTable)
            Log.v(Constants.LOG_TAG, userTable)
            db?.execSQL(tranTable)
            Log.v(Constants.LOG_TAG, tranTable)
        } catch (e: SQLiteException) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${Constants.UserColumn.TABLE_NAME};")
        db?.execSQL("DROP TABLE IF EXISTS ${Constants.TranColumn.TABLE_NAME};")
        onCreate(db)
    }

    fun insertUserProfileData(userProfile: UserProfile): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result: Long = 0.toLong()
        try {
            result = database.insert(
                Constants.UserColumn.TABLE_NAME,
                null,
                cvUser(userProfile, true)
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.UserColumn.USER_TABLE_URI, null)
        return result != (-1).toLong() // If -1 return it means not successfully inserted
    }

    fun updateUserProfileData(userProfile: UserProfile, id: Long): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result = 0
        try {
            result = database.update(
                Constants.UserColumn.TABLE_NAME,
                cvUser(userProfile, false),
                "${BaseColumns._ID} = ?",
                arrayOf(id.toString())
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.UserColumn.USER_TABLE_URI, null)
        return result != 0 // If 0 return it means not successfully updated
    }

    fun deleteUserProfileData(id: Long): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result = 0
        try {
            result = database.delete(
                Constants.UserColumn.TABLE_NAME,
                "${BaseColumns._ID} = ?",
                arrayOf(id.toString())
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.UserColumn.USER_TABLE_URI, null)
        return result != 0
    }

    fun searchUsersName(keyword: String): Cursor {
        val database: SQLiteDatabase = this.readableDatabase
        val query =
            "SELECT * FROM ${Constants.UserColumn.TABLE_NAME} WHERE ${Constants.UserColumn.NAME} LIKE ?"
        val mCursor: Cursor = database.rawQuery(query, arrayOf("%$keyword%"))
        try {
            mCursor.moveToNext()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return mCursor
    }

    fun getAllUserProfileData(): Cursor {
        val database: SQLiteDatabase = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            Constants.UserColumn.NAME,
            Constants.UserColumn.GENDER,
            Constants.UserColumn.ADDRESS,
            Constants.UserColumn.CITY,
            Constants.UserColumn.PHONE1,
            Constants.UserColumn.PHONE2,
            Constants.UserColumn.EMAIL,
            Constants.UserColumn.COMMENTS,
            Constants.UserColumn.CREATED_ON,
            Constants.UserColumn.LAST_MODIFIED
        )
        val cursor: Cursor = database.query(
            Constants.UserColumn.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        try {
            cursor.moveToNext()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return cursor
    }

    fun insertTransactions(transactions: Transactions): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result: Long = 0.toLong()
        try {
            result = database.insert(
                Constants.TranColumn.TABLE_NAME,
                null,
                cvTransaction(transactions, false)
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.TranColumn.TRANSACTION_TABLE_URI, null)
        return result != (-1).toLong() // If -1 return it means not successfully inserted
    }

    fun updateTransactions(id: Long, transactions: Transactions): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result = 0
        try {
            result = database.update(
                Constants.TranColumn.TABLE_NAME,
                cvTransaction(transactions, true),
                "${BaseColumns._ID} = ?",
                arrayOf(id.toString())
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.TranColumn.TRANSACTION_TABLE_URI, null)
        return result != 0 // If 0 return it means not successfully updated
    }

    fun deleteTransactions(id: Long): Boolean {
        val database: SQLiteDatabase = this.writableDatabase
        database.beginTransaction()
        var result = 0
        try {
            result = database.delete(
                Constants.TranColumn.TABLE_NAME,
                "${BaseColumns._ID} = ?",
                arrayOf(id.toString())
            )
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            database.setTransactionSuccessful()
            database.endTransaction()
            database.close()
        }
        mContext.contentResolver.notifyChange(Constants.TranColumn.TRANSACTION_TABLE_URI, null)
        return result != 0
    }

    fun searchTransDesc(id: Long, keyword: String): Cursor {
        val database: SQLiteDatabase = this.readableDatabase
        val query =
            "SELECT * FROM ${Constants.TranColumn.TABLE_NAME} WHERE ${Constants.TranColumn.USER_ID} = $id AND ${Constants.TranColumn.DESCRIPTION} LIKE ?"
        val mCursor: Cursor = database.rawQuery(query, arrayOf("%$keyword%"))
        try {
            mCursor.moveToNext()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return mCursor
    }

    fun getAllTransactionsByUserId(mUserId: Long, orderBy: String? = null): Cursor {
        val database: SQLiteDatabase = this.readableDatabase
        val projections = arrayOf(
            BaseColumns._ID,
            Constants.TranColumn.USER_ID,
            Constants.TranColumn.DATE,
            Constants.TranColumn.DESCRIPTION,
            Constants.TranColumn.CREDIT,
            Constants.TranColumn.DEBIT,
            Constants.TranColumn.BALANCE,
            Constants.TranColumn.CREATED_ON,
            Constants.TranColumn.LAST_MODIFIED
        )
        val cursor: Cursor = database.query(
            Constants.TranColumn.TABLE_NAME,
            projections,
            "${Constants.TranColumn.USER_ID} = ?",
            arrayOf(mUserId.toString()),
            null,
            null,
            orderBy
        )
        try {
            cursor.moveToNext()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return cursor
    }

    fun getPreviousBalanceByUserId(id: Long): Double {
        var previousBalance: Double = 0.toDouble()
        val getBalanceFromDatabase: SQLiteDatabase = this.readableDatabase
        val userID =
            "SELECT * FROM ${Constants.TranColumn.TABLE_NAME} WHERE ${Constants.TranColumn.USER_ID} = $id;"
        val cursor: Cursor = getBalanceFromDatabase.rawQuery(userID, null)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                if (cursor.count > 0) {
                    previousBalance =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.TranColumn.BALANCE))
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            cursor.close()
            getBalanceFromDatabase.close()
        }
        return previousBalance
    }

    fun getSumForColumns(id: Long, nameColumn: String, isAllAccountsSum: Boolean = false): Double {
        var sum: Double = 0.toDouble()
        var sumForColumn = ""
        when (nameColumn) {
            "Credit" -> {
                sumForColumn = Constants.TranColumn.CREDIT
            }
            "Debit" -> {
                sumForColumn = Constants.TranColumn.DEBIT
            }
            "Balance" -> {
                sumForColumn = Constants.TranColumn.BALANCE
            }
        }
        val getSumFromDatabase: SQLiteDatabase = this.readableDatabase
        val query: String = if (isAllAccountsSum) {
            "SELECT SUM ($sumForColumn) AS TOTAL FROM ${Constants.TranColumn.TABLE_NAME};"
        } else {
            "SELECT SUM ($sumForColumn) AS TOTAL FROM ${Constants.TranColumn.TABLE_NAME} WHERE ${Constants.TranColumn.USER_ID} = $id;"
        }
        val cursor: Cursor = getSumFromDatabase.rawQuery(query, null)
        try {
            if (cursor.moveToFirst()) {
                sum = cursor.getDouble(cursor.getColumnIndexOrThrow("TOTAL"))
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            cursor.close()
            getSumFromDatabase.close()
        }
        return sum
    }

    fun backupOrRestore(uri: Uri?, isBackup: Boolean) {
        val databaseFilePath = mContext.getDatabasePath(Constants.DATABASE_NAME).toString()
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            val databaseFile = File(databaseFilePath)
            if (isBackup) {
                inputStream = FileInputStream(databaseFile)
                // Open the empty db as the output stream
                outputStream = mContext.contentResolver.openOutputStream(uri!!)
            } else {
                inputStream = mContext.contentResolver.openInputStream(uri!!)
                // Open the empty db as the output stream
                outputStream = FileOutputStream(databaseFilePath)
            }
            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream!!.read(buffer).also { length = it } > 0) {
                outputStream!!.write(buffer, 0, length)
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
        } finally {
            if (isBackup) {
                HelperFunctions.makeToast(
                    mContext,
                    mContext.getString(R.string.backup_complete),
                    Toast.LENGTH_SHORT
                )
            } else {
                mContext.contentResolver.notifyChange(Constants.UserColumn.USER_TABLE_URI, null)
                HelperFunctions.makeToast(
                    mContext,
                    mContext.getString(R.string.restore_complete),
                    Toast.LENGTH_SHORT
                )
            }
            // Close the streams
            outputStream!!.flush()
            outputStream.close()
            inputStream?.close()
        }
    }

    fun generatePdf(uri: Uri, id: Long, userName: String): Boolean {
        val mDocument = Document()
        val mOrderBy: String = BaseColumns._ID + " ASC"
        val mCursor = getAllTransactionsByUserId(id, mOrderBy)
        try {
            val mOutPutStream = mContext.contentResolver.openOutputStream(uri)

            PdfWriter.getInstance(mDocument, mOutPutStream)
            mDocument.open()
            mDocument.addCreationDate()
            mDocument.addAuthor(mContext.getString(R.string.app_name))
            mDocument.addTitle("Transaction record for $userName")
            mDocument.addCreator(mContext.getString(R.string.app_name))

            val mParagraph = Paragraph()
            mParagraph.add(mContext.getString(R.string.app_name))
            mParagraph.add("$userName Account Statement")
            mDocument.add(mParagraph)

            val mTable = PdfPTable(5)
            mTable.addCell(Constants.TranColumn.DATE)
            mTable.addCell(Constants.TranColumn.DESCRIPTION)
            mTable.addCell(Constants.TranColumn.DEBIT)
            mTable.addCell(Constants.TranColumn.CREDIT)
            mTable.addCell(Constants.TranColumn.BALANCE)

            mCursor.moveToFirst()
            if (mCursor.moveToFirst()) do {
                val mDate: String = HelperFunctions.convertDateTimeShortFormat(
                    mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DATE))
                )
                val mDescription: String =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DESCRIPTION))
                val mCredit: Double =
                    mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREDIT))
                val mDebit: Double =
                    mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DEBIT))
                val mBalance: Double =
                    mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.BALANCE))
                mTable.addCell(mDate)
                mTable.addCell(mDescription)
                mTable.addCell(mCredit.toString())
                mTable.addCell(mDebit.toString())
                mTable.addCell(mBalance.toString())
            } while (mCursor.moveToNext())

            mDocument.add(mTable)
            return true
        } catch (de: DocumentException) {
            Log.e(Constants.LOG_TAG, de.message, de)
            FirebaseCrashlytics.getInstance().recordException(de)
            return false
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
            return false
        } finally {
            mDocument.close()
            mCursor.close()
        }
    }
}