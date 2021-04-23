package com.ahmer.accounting.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.adapter.GetAllTransactionsAdapter
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyCursorLoader
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.*

class UserTransactionsReport : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private lateinit var mAdapter: GetAllTransactionsAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTvTotalDeb: TextView
    private lateinit var mTvTotalCre: TextView
    private lateinit var mTvTotalBal: TextView
    private var mUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_rv)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        mUserId = intent.getIntExtra("mPosUserID", -1)
        val mUserName = intent.getStringExtra("mPosUserName")

        myDatabaseHelper = MyDatabaseHelper(this)

        val tvUserId = findViewById<TextView>(R.id.tvUserId)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fabAddTransaction)
        mTvTotalDeb = findViewById(R.id.tvTotalDebit)
        mTvTotalCre = findViewById(R.id.tvTotalCredit)
        mTvTotalBal = findViewById(R.id.tvTotalBalance)
        mRecyclerView = findViewById(R.id.rvGetAllRecords)

        tvUserId.text = mUserId.toString()
        tvUserName.text = mUserName

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mRecyclerView.recycledViewPool.clear()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = linearLayoutManager

        LoaderManager.getInstance(this).initLoader(1, null, this)

        fabAddTransaction.setOnClickListener {
            showAddTransactionDialog(it.context, mUserId)
        }
    }

    private fun showAddTransactionDialog(context: Context, userID: Int) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.transactions_add_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(true)
            val myDatabaseHelper = MyDatabaseHelper(context)
            val inputAmount = dialog.findViewById<TextInputEditText>(R.id.inputAmount)
            val radioGroupButton = dialog.findViewById<RadioGroup>(R.id.rgCreditDebit)
            val inputDate = dialog.findViewById<TextInputEditText>(R.id.inputDate)
            val inputDescription = dialog.findViewById<TextInputEditText>(R.id.inputDescription)
            val addTransactions = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)

            var typeAmount = ""
            radioGroupButton.setOnCheckedChangeListener { group, checkedId ->
                val radio = dialog.findViewById<RadioButton>(checkedId)
                typeAmount = radio.text.toString()
                Log.v(Constants.LOG_TAG, "TypeAmount: ${radio.text}")
            }

            val simpleDateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.UK)
            val currentDate = Calendar.getInstance()
            inputDate.setText(simpleDateFormat.format(currentDate.time))
            inputDate.setOnClickListener {
                val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    currentDate.set(year, month, dayOfMonth)
                    inputDate.setText(simpleDateFormat.format(currentDate.time))
                }
                val datePicker = DatePickerDialog(
                    context,
                    listener,
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH),
                    currentDate.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            addTransactions.setOnClickListener {
                var isSuccessfullyInserted = false
                var newAmount: Double = 0.toDouble()
                if (inputAmount.text.toString().trim().isNotEmpty()) {
                    newAmount = inputAmount.text.toString().trim().toDouble()
                }
                val newDate: String = inputDate.text.toString()
                val newDescription: String = inputDescription.text.toString().trim()

                when {
                    newAmount == 0.toDouble() -> {
                        HelperFunctions.makeToast(context, getString(R.string.enter_the_amount))
                    }
                    typeAmount.isEmpty() -> {
                        HelperFunctions.makeToast(context, getString(R.string.select_type_amount))
                    }
                    newDescription.trim().isEmpty() -> {
                        HelperFunctions.makeToast(
                            context,
                            getString(R.string.enter_transaction_description)
                        )
                    }
                    else -> {
                        val addNewTransaction = Transactions().apply {
                            userId = userID
                            credit = 0.toDouble()
                            debit = 0.toDouble()
                            if (typeAmount == getString(R.string.credit_plus)) {
                                credit = newAmount
                                balance += newAmount
                            }
                            if (typeAmount == getString(R.string.debit_minus)) {
                                debit = newAmount
                                balance -= newAmount
                            }
                            date = newDate
                            description = newDescription
                            created = HelperFunctions.getDateTime()
                        }
                        isSuccessfullyInserted =
                            myDatabaseHelper.insertTransactions(addNewTransaction)
                    }
                }
                if (isSuccessfullyInserted) {
                    HelperFunctions.makeToast(
                        context,
                        getString(R.string.transaction_added_successfully)
                    )
                    Thread.sleep(200)
                    dialog.dismiss()
                }
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return object : MyCursorLoader(applicationContext) {
            private val mObserver = ForceLoadContentObserver()
            override fun loadInBackground(): Cursor {
                val mCursor = myDatabaseHelper.getAllTransactionsByUserId(mUserId)
                mCursor.registerContentObserver(mObserver)
                mCursor.setNotificationUri(
                    contentResolver,
                    Constants.TranColumn.TRANSACTION_TABLE_URI
                )
                return mCursor
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        mAdapter = GetAllTransactionsAdapter(applicationContext, cursor!!)
        mRecyclerView.adapter = mAdapter

        val mUserCredit = myDatabaseHelper.getSumForColumns(mUserId, "Credit")
        val mUserDebit = myDatabaseHelper.getSumForColumns(mUserId, "Debit")
        val mUserBalance = myDatabaseHelper.getSumForColumns(mUserId, "Balance")
        mTvTotalDeb.text = mUserDebit.toString()
        mTvTotalCre.text = mUserCredit.toString()
        mTvTotalBal.text = mUserBalance.toString()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Keep empty
    }
}
