package com.ahmer.accounting.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.ahmer.accounting.user.UserDropDownAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTransactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_add)

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
        val tvDropDown = findViewById<MaterialAutoCompleteTextView>(R.id.tvDropDown)
        initialBalance.setText("0")
        creditAmount.setText("0")
        debitAmount.setText("0")

        val myDatabaseHelper = MyDatabaseHelper(this)
        val getUserProfileDataFromDatabase = myDatabaseHelper.getUserProfileData()
        val dropDownAdapter = UserDropDownAdapter(this, getUserProfileDataFromDatabase)
        var getUserId: Int
        var getUserPreviousBalance: Double

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

        tvDropDown.threshold = 1
        tvDropDown.setAdapter(dropDownAdapter)
        tvDropDown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                getUserId = getUserProfileDataFromDatabase[position].id
                Log.v(LOG_TAG, "UserId selected: $getUserId")
                getUserPreviousBalance = myDatabaseHelper.getPreviousBalance(getUserId)
                if (getUserPreviousBalance != 0.toDouble()) {
                    initialBalance.setText(getUserPreviousBalance.toString())
                    initialBalance.isEnabled = false
                    initialBalance.isClickable = false
                }

                tvDropDown.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        //Keep empty
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (initialBalance.length() >= 0 || creditAmount.length() >= 0 || debitAmount.length() >= 0) {
                            initialBalance.setText("0")
                            creditAmount.setText("0")
                            debitAmount.setText("0")
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        //Keep empty
                    }
                })

                saveButton.setOnClickListener {
                    var isSuccessfullyInserted = false
                    var isSuccessfullyUpdated = false

                    //if (transactionsId != getUserId) {
                    var newInitialBalance: Double =
                        initialBalance.text.toString().trim().toDouble()
                    val newCredit: Double = creditAmount.text.toString().trim().toDouble()
                    val newDebit: Double = debitAmount.text.toString().trim().toDouble()
                    val newDate: String = inputDate.text.toString()
                    val newDescription: String = inputDescription.text.toString().trim()

                    if (newCredit == 0.toDouble() && newDebit == 0.toDouble() && newInitialBalance == 0.toDouble()
                    ) {
                        HelperFunctions.makeToast(
                            it.context,
                            getString(R.string.must_enter_balance_credit_or_debit)
                        )
                    } else if (newDate.isEmpty()) {
                        HelperFunctions.makeToast(it.context, getString(R.string.pick_the_date))
                    } else if (newDescription.isEmpty()) {
                        HelperFunctions.makeToast(
                            it.context,
                            getString(R.string.enter_transaction_description)
                        )
                    } else {
                        val addNewTransaction = Transactions().apply {
                            userId = getUserId
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
                            date = newDate
                            description = newDescription
                            created = HelperFunctions.getDateTime()
                        }
                        isSuccessfullyInserted =
                            myDatabaseHelper.insertTransactions(addNewTransaction)
                    }
                    if (isSuccessfullyInserted) {
                        HelperFunctions.makeToast(
                            it.context,
                            getString(R.string.transaction_added_successfully)
                        )
                        Thread.sleep(200)
                        finish()
                    }
                    //}
                    /*else {
                        var updatedBalance: Double = getUserPreviousBalance
                        val updatedCredit: Double = creditAmount.text.toString().trim().toDouble()
                        val updatedDebit: Double = debitAmount.text.toString().trim().toDouble()
                        val updatedDate: String = inputDate.text.toString()
                        val updatedDescription: String = inputDescription.text.toString().trim()

                        if (updatedCredit == 0.toDouble() && updatedDebit == 0.toDouble()
                        ) {
                            HelperFunctions.makeToast(
                                it.context,
                                getString(R.string.must_enter_credit_or_debit)
                            )
                        } else if (updatedDate.isEmpty()) {
                            HelperFunctions.makeToast(it.context, getString(R.string.pick_the_date))
                        } else if (updatedDescription.isEmpty()) {
                            HelperFunctions.makeToast(
                                it.context,
                                getString(R.string.enter_transaction_description)
                            )
                        } else {
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

                                balance = updatedBalance
                                date = updatedDate
                                description = updatedDescription
                                modified = HelperFunctions.getDateTime()
                            }

                            isSuccessfullyUpdated =
                                myDatabaseHelper.updateTransactions(getUserId, updateTransactions)
                        }
                        if (isSuccessfullyUpdated) {
                            HelperFunctions.makeToast(
                                it.context,
                                getString(R.string.transaction_update_successfully)
                            )
                            Thread.sleep(200)
                            finish()
                        }
                    }*/
                }
            }
    }
}