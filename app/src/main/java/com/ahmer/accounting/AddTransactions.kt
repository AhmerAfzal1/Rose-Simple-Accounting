package com.ahmer.accounting

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.Constants.Companion.LOG_TAG
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTransactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_transactions)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        materialToolbar.setOnClickListener {
            finish()
        }

        val initialBalance = findViewById<TextInputEditText>(R.id.inputBalance)
        val creditAmount = findViewById<TextInputEditText>(R.id.inputCredit)
        val debitAmount = findViewById<TextInputEditText>(R.id.inputDebit)
        val inputDate = findViewById<TextInputEditText>(R.id.inputDate)
        val pickDate = findViewById<MaterialButton>(R.id.btnDate)
        val inputDescription = findViewById<TextInputEditText>(R.id.inputDescription)
        val saveButton = findViewById<MaterialButton>(R.id.btnSave)
        val tvSpinner = findViewById<MaterialAutoCompleteTextView>(R.id.tvSpinner)
        initialBalance.setText("0")
        creditAmount.setText("0")
        debitAmount.setText("0")

        val myDatabaseHelper = MyDatabaseHelper(this)
        val customerProfileFromDatabase = myDatabaseHelper.getCustomerProfileData()
        val customersAdapter = CustomersAdapter(this, customerProfileFromDatabase)
        var customerId: Int = 0
        var customerPreviousBalance: Double = 0.toDouble()

        pickDate.setOnClickListener {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.UK)
            val currentDate = Calendar.getInstance()
            val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                currentDate.set(year, month, dayOfMonth)
                inputDate.setText(simpleDateFormat.format(currentDate.time))
            }
            val datePicker = DatePickerDialog(
                this,
                listener,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        tvSpinner.threshold = 1
        tvSpinner.setAdapter(customersAdapter)
        tvSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                customerId = customerProfileFromDatabase[position].id
                customerPreviousBalance = myDatabaseHelper.getPreviousBalance(customerId)
                if (customerPreviousBalance != 0.toDouble()) {
                    initialBalance.setText(customerPreviousBalance.toString())
                    initialBalance.isEnabled = false
                    initialBalance.isClickable = false
                }
            }

        fun addTransaction(context: Context, iD: Int, previousBalance: Double) {
            Log.v(LOG_TAG, "Previous Balance: $previousBalance")
            val transactionsList = myDatabaseHelper.getTransactions()
            var transactionsId: Int = 0
            for (transaction in transactionsList) {
                transactionsId = transaction.id
            }
            try {
                if (transactionsId != iD) {
                    var newInitialBalance: Double = initialBalance.text.toString().trim().toDouble()
                    val newCredit: Double = creditAmount.text.toString().trim().toDouble()
                    val newDebit: Double = debitAmount.text.toString().trim().toDouble()
                    val newDate: String = inputDate.text.toString()
                    val newDescription: String = inputDescription.text.toString().trim()

                    val addNewTransaction = Transactions().apply {
                        id = iD

                        if (newCredit > 0) {
                            credit = newCredit
                            newInitialBalance += newCredit
                        } else {
                            0.toDouble()
                        }

                        if (newDebit > 0) {
                            debit = newDebit
                            newInitialBalance -= newDebit
                        } else {
                            0.toDouble()
                        }

                        balance = newInitialBalance

                        if (newDate.isNotEmpty()) {
                            date = newDate
                        } else {
                            Toast.makeText(context, "Please pick a date", Toast.LENGTH_SHORT).show()
                        }

                        if (newDescription.isNotEmpty()) {
                            description = newDescription
                        } else {
                            Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    myDatabaseHelper.insertTransactions(addNewTransaction)
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var updatedBalance: Double = previousBalance
                    val updatedCredit: Double = creditAmount.text.toString().trim().toDouble()
                    val updatedDebit: Double = debitAmount.text.toString().trim().toDouble()

                    val updateTransactions = Transactions().apply {
                        if (updatedCredit > 0) {
                            credit = updatedCredit
                            updatedBalance += updatedCredit
                        } else {
                            0.toDouble()
                        }

                        if (updatedDebit > 0) {
                            debit = updatedDebit
                            updatedBalance -= updatedDebit
                        } else {
                            0.toDouble()
                        }

                        if (updatedCredit == 0.toDouble() && updatedDebit == 0.toDouble()) {
                            Toast.makeText(
                                context,
                                "Please enter valid debit or credit amount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        balance = updatedBalance
                        date = inputDate.text.toString()
                        description = inputDescription.text.toString().trim()
                    }

                    myDatabaseHelper.updateTransactions(iD, updateTransactions)
                    Toast.makeText(context, "Transaction update successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, e.printStackTrace().toString())
            }
        }

        saveButton.setOnClickListener {
            addTransaction(this, customerId, customerPreviousBalance)
        }
    }
}