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

class TransactionsEdit(context: Context, trans: Transactions) : Dialog(context) {

    private val mContext = context
    private val mTransactions = trans
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

        var isSuccessfullyUpdated = false

        var newAccountType = ""
        mBinding.btnToggleGroupAmount.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            newAccountType = checkedButton.text.toString()
            Log.v(Constants.LOG_TAG, "AccountType: $newAccountType")
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (newAccountType) {
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

        val lastAmountType: String
        val lastAmountValue: String
        val oldAccountType: String = if (mTransactions.credit == 0.toDouble().toString()) {
            context.getString(R.string.debit_minus)
        } else {
            context.getString(R.string.credit_plus)
        }
        if (oldAccountType == context.getString(R.string.debit_minus)) {
            mBinding.btnToggleGroupAmount.check(R.id.toggleBtnDebit)
            mTransactions.enteredAmount = mTransactions.debit
            lastAmountType = "Debit"
            lastAmountValue = mTransactions.debit
        } else {
            mBinding.btnToggleGroupAmount.check(R.id.toggleBtnCredit)
            mTransactions.enteredAmount = mTransactions.credit
            lastAmountType = "Credit"
            lastAmountValue = mTransactions.credit
        }
        mBinding.btnAddTransaction.text = context.getString(R.string.update)
        mBinding.btnAddTransaction.setOnClickListener {
            val newAmount: Double = mTransactions.enteredAmount.trim().toDouble()
            when {
                newAmount == 0.toDouble() -> {
                    ToastUtils.showLong(context.getString(R.string.enter_the_amount))
                }
                oldAccountType.isEmpty() -> {
                    ToastUtils.showLong(context.getString(R.string.select_type_amount))
                }
                mTransactions.description.trim().isEmpty() -> {
                    ToastUtils.showLong(context.getString(R.string.enter_transaction_description))
                }
                else -> {
                    val transContentValues = Transactions().apply {
                        userId = mTransactions.userId
                        credit = 0.toDouble().toString()
                        debit = 0.toDouble().toString()
                        if (newAccountType == context.getString(R.string.credit_plus)) {
                            credit = newAmount.toString()
                            isDebit = false
                        }
                        if (newAccountType == context.getString(R.string.debit_minus)) {
                            debit = newAmount.toString()
                            isDebit = true
                        }
                        date = mTransactions.date
                        description = mTransactions.description
                        modified = HelperFunctions.getDateTime()
                        modifiedAccountType = lastAmountType
                        modifiedValue = lastAmountValue
                    }
                    isSuccessfullyUpdated =
                        myDatabaseHelper.updateTransactions(
                            mTransactions.transId,
                            transContentValues
                        )
                }
            }
            if (isSuccessfullyUpdated) {
                ToastUtils.showShort(context.getString(R.string.transaction_updated_successfully))
                Thread.sleep(200)
                dismiss()
            }
        }
        mBinding.btnCancelTransaction.setOnClickListener {
            dismiss()
        }
    }
}