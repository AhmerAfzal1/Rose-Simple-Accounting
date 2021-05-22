package com.ahmer.accounting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.TransDialogAddBinding
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.button.MaterialButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

class TransactionsAdd(context: Context, userID: Long) : Dialog(context) {

    private val mContext = context
    private val mUserID = userID
    private val mTransactions = Transactions()
    private lateinit var mBinding: TransDialogAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.trans_dialog_add,
            null,
            false
        )
        setContentView(mBinding.root)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        setCancelable(false)

        val myDatabaseHelper = MyDatabaseHelper()
        mTransactions.date = HelperFunctions.getDateTime()

        mBinding.mAddEditDialog = mTransactions

        var typeAccount = ""
        mBinding.btnToggleGroupAmount.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            typeAccount = checkedButton.text.toString()
            Log.v(Constants.LOG_TAG, "TypeAmount: $typeAccount")
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (typeAccount) {
                        mContext.getString(R.string.credit_plus) -> {
                            mBinding.toggleBtnCredit.backgroundTintList =
                                ContextCompat.getColorStateList(mContext, R.color.black)
                            mBinding.toggleBtnDebit.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    mContext, android.R.color.transparent
                                )
                        }
                        else -> {
                            mBinding.toggleBtnCredit.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    mContext, android.R.color.transparent
                                )
                            mBinding.toggleBtnDebit.backgroundTintList =
                                ContextCompat.getColorStateList(mContext, R.color.black)
                        }
                    }
                }
            }
        }

        mBinding.inputDate.setOnClickListener {
            mTransactions.date = HelperFunctions.dateTimePickerShow(it)
        }

        mBinding.btnAddTransaction.setOnClickListener {
            var isSuccessfullyInserted = false
            var newAmount: Double = 0.toDouble()
            if (mTransactions.enteredAmount.trim().isNotEmpty()) {
                newAmount = mTransactions.enteredAmount.trim().toDouble()
            }
            val newDate: String = mTransactions.date
            val newDescription: String = mTransactions.description.trim()

            when {
                newAmount == 0.toDouble() -> {
                    ToastUtils.showLong(mContext.getString(R.string.enter_the_amount))
                }
                typeAccount.isEmpty() -> {
                    ToastUtils.showLong(mContext.getString(R.string.select_type_amount))
                }
                newDescription.trim().isEmpty() -> {
                    ToastUtils.showLong(mContext.getString(R.string.enter_transaction_description))
                }
                else -> {
                    val addNewTransaction = Transactions().apply {
                        userId = mUserID
                        credit = 0.toDouble().toString()
                        debit = 0.toDouble().toString()
                        if (typeAccount == mContext.getString(R.string.credit_plus)) {
                            credit = newAmount.toString()
                            isDebit = false
                        }
                        if (typeAccount == mContext.getString(R.string.debit_minus)) {
                            debit = newAmount.toString()
                            isDebit = true
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
                ToastUtils.showShort(mContext.getString(R.string.transaction_added_successfully))
                Thread.sleep(200)
                dismiss()
            }
        }

        mBinding.btnCancelTransaction.setOnClickListener {
            dismiss()
        }
    }
}