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
        val previousBalance: Double = myDatabaseHelper.getPreviousBalance()

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

        val customerProfileFromDatabase = myDatabaseHelper.getCustomerProfileData()
        val customersAdapter = CustomersAdapter(this, customerProfileFromDatabase)
        var customerId: Int = 0
        tvSpinner.threshold = 1
        tvSpinner.setAdapter(customersAdapter)
        tvSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                customerId = customerProfileFromDatabase[position].id
                when (position) {
                    0 -> {
                        Log.v(LOG_TAG, "Ahmer")
                    }
                    1 -> {
                        Log.v(LOG_TAG, "Aamir")
                    }
                }
            }

        fun addTransaction(context: Context, iD: Int) {
            try {
                if (previousBalance > 0) {
                    initialBalance.setText(previousBalance.toString())
                    initialBalance.isEnabled = false
                    initialBalance.isClickable = false
                    var newBalance: Double = 0.toDouble()
                    val newCredit: Double
                    val newDebit: Double
                    if (creditAmount.text.toString().trim().toDouble() > 0) {
                        newCredit = creditAmount.text.toString().trim().toDouble()
                        newBalance += newCredit
                    } else {
                        newCredit = 0.toDouble()
                    }
                    if (debitAmount.text.toString().trim().toDouble() > 0) {
                        newDebit = debitAmount.text.toString().trim().toDouble()
                        newBalance -= newDebit
                    } else {
                        newDebit = 0.toDouble()
                    }

                    if (newCredit == 0.toDouble() && newDebit == 0.toDouble()) {
                        Toast.makeText(
                            context,
                            "Please enter valid debit or credit amount",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val transactions = Transactions().apply {
                        balance = newBalance
                        credit = newCredit
                        debit = newDebit
                        date = inputDate.text.toString()
                        description = inputDescription.text.toString().trim()
                    }
                    myDatabaseHelper.insertTransactions(transactions)
                    Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var userInputBalance: Double
                    var userInputCredit: Double
                    var userInputDebit: Double
                    val userInputDate: String = inputDate.text.toString()
                    val userInputDescription: String = inputDescription.text.toString().trim()
                    val transactions = Transactions().apply {
                        id = iD
                        if (initialBalance.text.toString().trim().toDouble() > 0) {
                            userInputBalance = initialBalance.text.toString().trim().toDouble()
                            balance = userInputBalance
                        } else {
                            balance = 0.toDouble()
                        }
                        if (creditAmount.text.toString().trim().toDouble() > 0) {
                            userInputCredit = creditAmount.text.toString().trim().toDouble()
                            credit = userInputCredit
                        } else {
                            credit = 0.toDouble()
                        }
                        if (creditAmount.text.toString().trim().toDouble() > 0) {
                            userInputDebit = debitAmount.text.toString().trim().toDouble()
                            debit = userInputDebit
                        } else {
                            debit = 0.toDouble()
                        }
                        if (userInputDate.isNotEmpty()) {
                            date = userInputDate
                        } else {
                            Toast.makeText(context, "Please pick a date", Toast.LENGTH_SHORT).show()
                        }
                        if (userInputDescription.isNotEmpty()) {
                            description = userInputDescription
                        } else {
                            Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    myDatabaseHelper.insertTransactions(transactions)
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, e.printStackTrace().toString())
            }
        }

        saveButton.setOnClickListener {
            addTransaction(this, customerId)
        }
    }
}