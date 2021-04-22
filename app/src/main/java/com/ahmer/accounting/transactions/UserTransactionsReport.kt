package com.ahmer.accounting.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.*

class UserTransactionsReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_rv)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        val mUserId = intent.getIntExtra("mPosUserID", -1)
        val mUserName = intent.getStringExtra("mPosUserName")
        val mUserDebit = intent.getDoubleExtra("mPosUserDebit", 0.toDouble())
        val mUserCredit = intent.getDoubleExtra("mPosUserCredit", 0.toDouble())
        Log.v(Constants.LOG_TAG, "ID: $mUserId, Name: $mUserName")

        val tvUserId = findViewById<TextView>(R.id.tvUserId)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvTotalDeb = findViewById<TextView>(R.id.tvTotalDebit)
        val tvTotalCre = findViewById<TextView>(R.id.tvTotalCredit)
        val tvTotalBal = findViewById<TextView>(R.id.tvTotalBalance)
        val recyclerView = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fabAddTransaction)

        tvUserId.text = mUserId.toString()
        tvUserName.text = mUserName
        tvTotalDeb.text = mUserDebit.toString()
        tvTotalCre.text = mUserCredit.toString()
        val totalBalance = mUserCredit - mUserDebit
        tvTotalBal.text = totalBalance.toString()

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GetTransactionsStatementAdapter(this, mUserId)

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
            dialog.setCancelable(false)
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
                Log.v(Constants.LOG_TAG, "TypeAmount2: ${radio.text}")
            }

            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.UK)
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
}

class GetTransactionsStatementAdapter(context: Context, mUserId: Int) :
    RecyclerView.Adapter<TransactionsViewHolder>() {

    private val mContext = context
    private val myDatabaseHelper = MyDatabaseHelper(mContext)
    private val mTransactionsList = myDatabaseHelper.getTransactionsByUserId(mUserId)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_add_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bindView(mTransactionsList[position])
    }

    override fun getItemCount(): Int {
        return mTransactionsList.size
    }
}

class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(transactions: Transactions) {
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvDeb = itemView.findViewById<TextView>(R.id.tvDebit)
        val tvCre = itemView.findViewById<TextView>(R.id.tvCredit)
        tvDate.text = transactions.date
        tvDesc.text = transactions.description
        if (transactions.debit == 0.toDouble()) {
            tvDeb.text = ""
        } else {
            tvDeb.text = transactions.debit.toString()
        }
        if (transactions.credit == 0.toDouble()) {
            tvCre.text = ""
        } else {
            tvCre.text = transactions.credit.toString()
        }
    }
}
