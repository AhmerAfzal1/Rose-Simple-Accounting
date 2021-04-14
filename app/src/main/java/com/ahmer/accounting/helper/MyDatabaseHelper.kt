package com.ahmer.accounting.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ahmer.accounting.helper.Constants.Companion.DATABASE_NAME
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.model.Transactions
import com.ahmer.accounting.model.UserProfile
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION: Int = 1
        private const val ID: String = "ID"
        private const val CREATED_DATETIME: String = "Created"
        private const val MODIFIED_DATETIME: String = "LastModified"

        private const val USER_TABLE_NAME: String = "Customers"
        private const val USER_NAME: String = "Name"
        private const val USER_GENDER: String = "Gender"
        private const val USER_ADDRESS: String = "Address"
        private const val USER_CITY: String = "City"
        private const val USER_PHONE1: String = "Phone1"
        private const val USER_PHONE2: String = "Phone2"
        private const val USER_EMAIL: String = "Email"
        private const val USER_COMMENTS: String = "Comments"

        private const val TRANSACTIONS_TABLE_NAME: String = "Transactions"
        private const val USER_ID: String = "UserID"
        private const val DATE: String = "Date"
        private const val DESCRIPTION: String = "Description"
        private const val CREDIT: String = "Credit"
        private const val DEBIT: String = "Debit"
        private const val BALANCE: String = "Balance"

        fun dataValuesUserProfile(
            userProfile: UserProfile,
            isCreated: Boolean = true
        ): ContentValues {
            return ContentValues().apply {
                put(USER_NAME, userProfile.name)
                put(USER_GENDER, userProfile.gender)
                put(USER_ADDRESS, userProfile.address)
                put(USER_CITY, userProfile.city)
                put(USER_PHONE1, userProfile.phone1)
                put(USER_PHONE2, userProfile.phone2)
                put(USER_EMAIL, userProfile.email)
                put(USER_COMMENTS, userProfile.comment)
                if (isCreated) {
                    put(CREATED_DATETIME, userProfile.created)
                } else {
                    put(MODIFIED_DATETIME, userProfile.modified)
                }
            }
        }

        fun dataValuesTransactions(
            transactions: Transactions,
            isModified: Boolean = false
        ): ContentValues {
            return ContentValues().apply {
                put(USER_ID, transactions.userId)
                put(DATE, transactions.date)
                put(DESCRIPTION, transactions.description)
                put(CREDIT, transactions.credit)
                put(DEBIT, transactions.debit)
                put(BALANCE, transactions.balance)
                if (isModified) {
                    put(MODIFIED_DATETIME, transactions.modified)
                } else {
                    put(CREATED_DATETIME, transactions.created)
                }
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
            val createUserProfileTable = "CREATE TABLE IF NOT EXISTS $USER_TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "$USER_NAME TEXT NOT NULL, " +
                    "$USER_GENDER VARCHAR(7) NOT NULL, " +
                    "$USER_ADDRESS TEXT, " +
                    "$USER_CITY TEXT, " +
                    "$USER_PHONE1 TEXT, " +
                    "$USER_PHONE2 TEXT, " +
                    "$USER_EMAIL TEXT, " +
                    "$USER_COMMENTS TEXT, " +
                    "$CREATED_DATETIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "$MODIFIED_DATETIME TIMESTAMP DEFAULT \"\", " +
                    "CHECK($USER_GENDER IN ('Male', 'Female', 'Unknown'))" +
                    ");"
            val createTransactionsTable = "CREATE TABLE IF NOT EXISTS $TRANSACTIONS_TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "$USER_ID INTEGER, " +
                    "$DATE DATE DEFAULT CURRENT_DATE NOT NULL, " +
                    "$DESCRIPTION TEXT NOT NULL, " +
                    "$CREDIT REAL, " +
                    "$DEBIT REAL, " +
                    "$BALANCE REAL, " +
                    "$CREATED_DATETIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "$MODIFIED_DATETIME TIMESTAMP DEFAULT \"\", " +
                    "FOREIGN KEY ($USER_ID) REFERENCES $USER_TABLE_NAME($ID)" +
                    ");"
            db?.execSQL("PRAGMA foreign_keys = ON;")
            db?.execSQL(createUserProfileTable)
            Log.v(LOG_TAG, createUserProfileTable)
            Thread.sleep(200)
            db?.execSQL(createTransactionsTable)
            Log.v(LOG_TAG, createTransactionsTable)
        } catch (e: SQLiteException) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME;")
        db?.execSQL("DROP TABLE IF EXISTS $TRANSACTIONS_TABLE_NAME;")
        onCreate(db)
    }

    fun insertUserProfileData(userProfile: UserProfile): Boolean {
        val writeDatabase: SQLiteDatabase = this.writableDatabase
        try {
            writeDatabase.insert(
                USER_TABLE_NAME,
                null,
                dataValuesUserProfile(userProfile, true)
            )
            return true
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            writeDatabase.close()
        }
        return false
    }

    fun updateUserProfileData(userProfile: UserProfile, id: Int): Boolean {
        val updateWriteDatabase = this.writableDatabase
        try {
            updateWriteDatabase.update(
                USER_TABLE_NAME,
                dataValuesUserProfile(userProfile, false),
                "$ID = ?",
                arrayOf(id.toString())
            )
            return true
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            updateWriteDatabase.close()
        }
        return false
    }

    fun getUserProfileData(): ArrayList<UserProfile> {
        val readDatabase: SQLiteDatabase = this.readableDatabase
        val userProfileList = ArrayList<UserProfile>()
        val queryContent = arrayOf(
            ID,
            USER_NAME,
            USER_GENDER,
            USER_ADDRESS,
            USER_CITY,
            USER_PHONE1,
            USER_PHONE2,
            USER_EMAIL,
            USER_COMMENTS,
            CREATED_DATETIME,
            MODIFIED_DATETIME
        )
        try {
            val cursor: Cursor =
                readDatabase.query(
                    USER_TABLE_NAME,
                    queryContent,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            try {
                while (cursor.moveToNext()) {
                    val userProfile = UserProfile()
                    userProfile.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                    userProfile.name =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME))
                    userProfile.gender =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_GENDER))
                    userProfile.address =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_ADDRESS))
                    userProfile.city =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_CITY))
                    userProfile.phone1 =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE1))
                    userProfile.phone2 =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE2))
                    userProfile.email =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL))
                    userProfile.comment =
                        cursor.getString(cursor.getColumnIndexOrThrow(USER_COMMENTS))
                    userProfile.created =
                        cursor.getString(cursor.getColumnIndexOrThrow(CREATED_DATETIME))
                    userProfile.modified =
                        cursor.getString(cursor.getColumnIndexOrThrow(MODIFIED_DATETIME))
                    userProfileList.add(userProfile)
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("GetUserProfileData $ID: ")
                        .append(cursor.getInt(cursor.getColumnIndexOrThrow(ID)))
                    stringBuilder.append("\nGetUserProfileData $USER_NAME: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)))
                    stringBuilder.append("\nGetUserProfileData $USER_GENDER: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_GENDER)))
                    stringBuilder.append("\nGetUserProfileData $USER_ADDRESS: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_ADDRESS)))
                    stringBuilder.append("\nGetUserProfileData $USER_CITY: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_CITY)))
                    stringBuilder.append("\nGetUserProfileData $USER_PHONE1: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE1)))
                    stringBuilder.append("\nGetUserProfileData $USER_PHONE2: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE2)))
                    stringBuilder.append("\nGetUserProfileData $USER_EMAIL: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)))
                    stringBuilder.append("\nGetUserProfileData $USER_COMMENTS: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(USER_COMMENTS)))
                    stringBuilder.append("\nGetUserProfileData $CREATED_DATETIME: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CREATED_DATETIME)))
                    stringBuilder.append("\nGetUserProfileData $MODIFIED_DATETIME: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(MODIFIED_DATETIME)))
                    Log.v(LOG_TAG, stringBuilder.toString())
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                cursor.close()
                readDatabase.close()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return userProfileList
    }

    fun insertTransactions(transactions: Transactions): Boolean {
        val writeDatabase = this.writableDatabase
        try {
            writeDatabase.insert(
                TRANSACTIONS_TABLE_NAME,
                null,
                dataValuesTransactions(transactions, false)
            )
            return true
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            writeDatabase.close()
        }
        return false
    }

    fun getTransactionsByUserId(mUserId: Int): ArrayList<Transactions> {
        val transactions = ArrayList<Transactions>()
        val getFromDatabase = this.readableDatabase
        val projections = arrayOf(
            ID,
            USER_ID,
            DATE,
            DESCRIPTION,
            CREDIT,
            DEBIT,
            BALANCE,
            CREATED_DATETIME,
            MODIFIED_DATETIME
        )
        try {
            val cursor: Cursor = getFromDatabase.query(
                TRANSACTIONS_TABLE_NAME,
                projections,
                "$USER_ID = ?",
                arrayOf(mUserId.toString()),
                null,
                null,
                null
            )
            try {
                while (cursor.moveToNext()) {
                    val transaction = Transactions().apply {
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID))
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                        credit = cursor.getDouble(cursor.getColumnIndexOrThrow(CREDIT))
                        debit = cursor.getDouble(cursor.getColumnIndexOrThrow(DEBIT))
                        balance = cursor.getDouble(cursor.getColumnIndexOrThrow(BALANCE))
                        created = cursor.getString(cursor.getColumnIndexOrThrow(CREATED_DATETIME))
                        modified = cursor.getString(cursor.getColumnIndexOrThrow(MODIFIED_DATETIME))
                    }
                    transactions.add(transaction)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                cursor.close()
                getFromDatabase.close()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return transactions
    }

    fun updateTransactions(id: Int, transactions: Transactions): Boolean {
        val updateTransactions = this.writableDatabase
        try {
            updateTransactions.update(
                TRANSACTIONS_TABLE_NAME,
                dataValuesTransactions(transactions, true),
                "$ID = ?",
                arrayOf(id.toString())
            )
            return true
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            updateTransactions.close()
        }
        return false
    }

    fun getPreviousBalanceByUserId(id: Int): Double {
        var previousBalance: Double = 0.toDouble()
        val getBalanceFromDatabase = this.readableDatabase
        val userID = "SELECT * FROM $TRANSACTIONS_TABLE_NAME WHERE $USER_ID = $id;"
        val cursor: Cursor = getBalanceFromDatabase.rawQuery(userID, null)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                if (cursor.count > 0) {
                    previousBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(BALANCE))
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally {
            cursor.close()
            getBalanceFromDatabase.close()
        }
        return previousBalance
    }
}