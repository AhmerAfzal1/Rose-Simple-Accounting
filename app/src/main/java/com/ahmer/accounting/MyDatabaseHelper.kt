package com.ahmer.accounting

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ahmer.accounting.Constants.Companion.LOG_TAG
import com.ahmer.accounting.model.CustomerProfile
import com.ahmer.accounting.model.Transactions

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME: String = "SimpleAccounting"
        private const val DATABASE_VERSION: Int = 1
        private const val ID: String = "ID"

        private const val CUSTOMER_TABLE_NAME: String = "Customers"
        private const val CUSTOMER_NAME: String = "Name"
        private const val CUSTOMER_GENDER: String = "Gender"
        private const val CUSTOMER_ADDRESS: String = "Address"
        private const val CUSTOMER_CITY: String = "City"
        private const val CUSTOMER_PHONE1: String = "Phone1"
        private const val CUSTOMER_PHONE2: String = "Phone2"
        private const val CUSTOMER_PHONE3: String = "Phone3"
        private const val CUSTOMER_COMMENTS: String = "Comments"

        private const val TRANSACTIONS_TABLE_NAME: String = "Transactions"
        private const val DATE: String = "Date"
        private const val DESCRIPTION: String = "Description"
        private const val CREDIT: String = "Credit"
        private const val DEBIT: String = "Debit"
        private const val BALANCE: String = "Balance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            val createCustomersTable = "CREATE TABLE IF NOT EXISTS $CUSTOMER_TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "$CUSTOMER_NAME TEXT NOT NULL, " +
                    "$CUSTOMER_GENDER VARCHAR(7) NOT NULL, " +
                    "$CUSTOMER_ADDRESS TEXT, " +
                    "$CUSTOMER_CITY TEXT, " +
                    "$CUSTOMER_PHONE1 TEXT, " +
                    "$CUSTOMER_PHONE2 TEXT, " +
                    "$CUSTOMER_PHONE3 TEXT, " +
                    "$CUSTOMER_COMMENTS TEXT, " +
                    "CHECK($CUSTOMER_GENDER IN ('Male', 'Female', 'Unknown'))" +
                    ")"
            val createTransactionsTable = "CREATE TABLE IF NOT EXISTS $TRANSACTIONS_TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                    "$DATE DATE DEFAULT CURRENT_DATE NOT NULL, " +
                    "$DESCRIPTION TEXT NOT NULL, " +
                    "$CREDIT REAL, " +
                    "$DEBIT REAL, " +
                    "$BALANCE REAL" +
                    ")"
            Log.v(LOG_TAG, "$createCustomersTable \n $createTransactionsTable")
            db?.execSQL(createCustomersTable)
            db?.execSQL(createTransactionsTable)
        } catch (e: SQLiteException) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $CUSTOMER_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TRANSACTIONS_TABLE_NAME")
        onCreate(db)
    }

    fun insertCustomerProfileData(customerProfile: CustomerProfile) {
        val dataValues = ContentValues().apply {
            put(CUSTOMER_NAME, customerProfile.name)
            put(CUSTOMER_GENDER, customerProfile.gender)
            put(CUSTOMER_ADDRESS, customerProfile.address)
            put(CUSTOMER_CITY, customerProfile.city)
            put(CUSTOMER_PHONE1, customerProfile.phone1)
            put(CUSTOMER_PHONE2, customerProfile.phone2)
            put(CUSTOMER_PHONE3, customerProfile.phone3)
            put(CUSTOMER_COMMENTS, customerProfile.comment)
        }
        val writeDatabase: SQLiteDatabase = this.writableDatabase
        writeDatabase.insert(CUSTOMER_TABLE_NAME, null, dataValues)
        writeDatabase.close()
    }

    fun getCustomerProfileData(): ArrayList<CustomerProfile> {
        val readDatabase: SQLiteDatabase = this.readableDatabase
        val customersList = ArrayList<CustomerProfile>()
        val queryContent = arrayOf<String>(
            ID,
            CUSTOMER_NAME,
            CUSTOMER_GENDER,
            CUSTOMER_ADDRESS,
            CUSTOMER_CITY,
            CUSTOMER_PHONE1,
            CUSTOMER_PHONE2,
            CUSTOMER_PHONE3,
            CUSTOMER_COMMENTS
        )
        try {
            val cursor: Cursor =
                readDatabase.query(
                    CUSTOMER_TABLE_NAME,
                    queryContent,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            try {
                while (cursor.moveToNext()) {
                    val customerProfile = CustomerProfile()
                    customerProfile.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                    customerProfile.name =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_NAME))
                    customerProfile.gender =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_GENDER))
                    customerProfile.address =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_ADDRESS))
                    customerProfile.city =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_CITY))
                    customerProfile.phone1 =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE1))
                    customerProfile.phone2 =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE2))
                    customerProfile.phone3 =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE3))
                    customerProfile.comment =
                        cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_COMMENTS))
                    customersList.add(customerProfile)
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("ID: ")
                        .append(cursor.getInt(cursor.getColumnIndexOrThrow(ID)))
                    stringBuilder.append("\nName: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_NAME)))
                    stringBuilder.append("\nGender: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_GENDER)))
                    stringBuilder.append("\nAddress: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_ADDRESS)))
                    stringBuilder.append("\nCity: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_CITY)))
                    stringBuilder.append("\nPhone1: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE1)))
                    stringBuilder.append("\nPhone2: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE2)))
                    stringBuilder.append("\nPhone3: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_PHONE3)))
                    stringBuilder.append("\nComments: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMER_COMMENTS)))
                    Log.v(LOG_TAG, stringBuilder.toString())
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, e.printStackTrace().toString())
            } finally {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }

        return customersList
    }

    fun insertTransactions(transactions: Transactions) {
        val contentValues = ContentValues().apply {
            put(ID, transactions.id)
            put(DATE, transactions.date)
            put(DESCRIPTION, transactions.description)
            put(CREDIT, transactions.credit)
            put(DEBIT, transactions.debit)
            put(BALANCE, transactions.balance)
        }

        val writeDatabase = this.writableDatabase
        writeDatabase.insert(TRANSACTIONS_TABLE_NAME, null, contentValues)
        writeDatabase.close()
    }

    fun getTransactions(): ArrayList<Transactions> {
        val transactions = ArrayList<Transactions>()
        val getFromDatabase = this.readableDatabase
        val columnsArray = arrayOf<String>(
            ID, DATE, DESCRIPTION, CREDIT, DEBIT, BALANCE
        )
        try {
            val cursor: Cursor = getFromDatabase.query(
                TRANSACTIONS_TABLE_NAME,
                columnsArray,
                null,
                null,
                null,
                null,
                null
            )
            try {
                while (cursor.moveToNext()) {
                    val transaction = Transactions().apply {
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                        credit = cursor.getDouble(cursor.getColumnIndexOrThrow(CREDIT))
                        debit = cursor.getDouble(cursor.getColumnIndexOrThrow(DEBIT))
                        balance = cursor.getDouble(cursor.getColumnIndexOrThrow(BALANCE))
                    }
                    transactions.add(transaction)
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("ID: ")
                        .append(cursor.getInt(cursor.getColumnIndexOrThrow(ID)))
                    stringBuilder.append("\nDate: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(DATE)))
                    stringBuilder.append("\nDescription: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)))
                    stringBuilder.append("\nCredit: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(CREDIT)))
                    stringBuilder.append("\nDebit: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(DEBIT)))
                    stringBuilder.append("\nBalance: ")
                        .append(cursor.getString(cursor.getColumnIndexOrThrow(BALANCE)))
                    Log.v(LOG_TAG, stringBuilder.toString())
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, e.printStackTrace().toString())
            } finally {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }
        return transactions
    }

    fun getPreviousBalance(): Double {
        var previousBalance: Double = 0.toDouble()
        val getBalanceFromDatabase = this.readableDatabase
        val columnsArray = arrayOf<String>(
            ID, DATE, DESCRIPTION, CREDIT, DEBIT, BALANCE
        )
        try {
            val cursor: Cursor = getBalanceFromDatabase.query(
                TRANSACTIONS_TABLE_NAME,
                columnsArray,
                null,
                null,
                null,
                null,
                null
            )
            try {
                while (cursor.moveToNext()) {
                    /*val transaction = Transactions().apply {
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                        credit = cursor.getDouble(cursor.getColumnIndexOrThrow(CREDIT))
                        debit = cursor.getDouble(cursor.getColumnIndexOrThrow(DEBIT))
                        balance = cursor.getDouble(cursor.getColumnIndexOrThrow(BALANCE))
                    }*/
                    previousBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(BALANCE))
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, e.printStackTrace().toString())
            } finally {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }

        return previousBalance
    }
}