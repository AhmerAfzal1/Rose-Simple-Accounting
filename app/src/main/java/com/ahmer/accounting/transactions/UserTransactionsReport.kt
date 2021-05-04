package com.ahmer.accounting.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.adapter.TransactionsAdapter
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyCursorLoader
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.*


class UserTransactionsReport : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mAdapter: TransactionsAdapter
    private lateinit var mCvTotalBal: MaterialCardView
    private lateinit var mLayoutNoTransaction: LinearLayout
    private lateinit var mLayoutSubTotal: LinearLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTvTotalBal: TextView
    private lateinit var mTvTotalBalHeading: TextView
    private lateinit var mTvTotalCre: TextView
    private lateinit var mTvTotalDeb: TextView
    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private var mUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_rv)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        mUserId = intent.getLongExtra("mPosUserID", -1)
        val mUserName = intent.getStringExtra("mPosUserName")
        val mUserPhone = intent.getStringExtra("mPosUserPhone")

        myDatabaseHelper = MyDatabaseHelper(this)

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserPhone = findViewById<TextView>(R.id.tvUserPhone)
        mLayoutNoTransaction = findViewById(R.id.layoutNoTransaction)
        mLayoutSubTotal = findViewById(R.id.layoutSubTotal)
        mTvTotalDeb = findViewById(R.id.tvTotalDebit)
        mTvTotalCre = findViewById(R.id.tvTotalCredit)
        mCvTotalBal = findViewById(R.id.cvTotalBalance)
        mTvTotalBalHeading = findViewById(R.id.tvTotalBalanceHeading)
        mTvTotalBal = findViewById(R.id.tvTotalBalance)
        mRecyclerView = findViewById(R.id.rvGetAllRecords)

        tvUserPhone.text = mUserPhone
        tvUserName.text = mUserName

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mRecyclerView.recycledViewPool.clear()
        mRecyclerView.clearOnScrollListeners()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = linearLayoutManager

        LoaderManager.getInstance(this).initLoader(1, null, this)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_add_trans -> {
                    showAddTransactionDialog(this, mUserId)
                }
                R.id.menu_search_trans_desc -> {
                    val searchView: SearchView = it?.actionView as SearchView
                    val editText: EditText = searchView.findViewById(R.id.search_src_text)
                    editText.setTextColor(Color.WHITE)
                    editText.setHintTextColor(Color.GRAY)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            searchTransactionDescription(query!!)
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            searchTransactionDescription(newText!!)
                            return false
                        }
                    })
                }
            }
            true
        }
    }

    private fun showAddTransactionDialog(context: Context, userID: Long) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.transactions_add_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            val myDatabaseHelper = MyDatabaseHelper(context)
            val inputAmount = dialog.findViewById<TextInputEditText>(R.id.inputAmount)
            val toggleGroup =
                dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
            val inputDate = dialog.findViewById<TextInputEditText>(R.id.inputDate)
            val inputDescription = dialog.findViewById<TextInputEditText>(R.id.inputDescription)
            val addTransactions = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
            val cancelTransactions = dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

            var typeAmount = ""
            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
                val checkedButton = dialog.findViewById<MaterialButton>(checkedId)
                typeAmount = checkedButton.text.toString()
                Log.v(Constants.LOG_TAG, "TypeAmount: $typeAmount")
            }

            val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
            val currentDate = Calendar.getInstance()
            inputDate.setText(dateFormat.format(currentDate.time))
            inputDate.setOnClickListener {
                val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    currentDate.set(year, month, dayOfMonth)
                    inputDate.setText(dateFormat.format(currentDate.time))
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
            cancelTransactions.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun searchTransactionDescription(keyword: String) {
        val cursor = myDatabaseHelper.search(
            Constants.TranColumn.TABLE_NAME,
            Constants.TranColumn.DESCRIPTION,
            keyword
        )
        mAdapter = TransactionsAdapter(applicationContext, cursor)
        mRecyclerView.adapter = mAdapter
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
        val context: Context = applicationContext
        mAdapter = TransactionsAdapter(this, cursor!!)
        if (mAdapter.itemCount > 0) {
            mLayoutNoTransaction.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
            mLayoutSubTotal.visibility = View.VISIBLE
        } else {
            mLayoutNoTransaction.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            mLayoutSubTotal.visibility = View.GONE
        }
        mRecyclerView.adapter = mAdapter

        val mUserCredit = myDatabaseHelper.getSumForColumns(mUserId, "Credit")
        val mUserDebit = myDatabaseHelper.getSumForColumns(mUserId, "Debit")
        val mUserBalance = myDatabaseHelper.getSumForColumns(mUserId, "Balance")
        val mTotalBalance = HelperFunctions.getRoundedValue(mUserBalance)
        if (mTotalBalance > "0") {
            mCvTotalBal.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenLight))
            mTvTotalBalHeading.setTextColor(ContextCompat.getColor(context, R.color.colorGreenDark))
            mTvTotalBal.setTextColor(ContextCompat.getColor(context, R.color.colorGreenDark))
        } else {
            mCvTotalBal.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRedLight))
            mTvTotalBalHeading.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))
            mTvTotalBal.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))
        }
        mTvTotalDeb.text = HelperFunctions.getRoundedValue(mUserDebit)
        mTvTotalCre.text = HelperFunctions.getRoundedValue(mUserCredit)
        mTvTotalBal.text = mTotalBalance
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Keep empty
    }

    override fun onDestroy() {
        super.onDestroy()
        myDatabaseHelper.close()
    }
}
