package com.ahmer.accounting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.TransDialogInfoBinding
import com.ahmer.accounting.model.Transactions
import com.google.firebase.crashlytics.FirebaseCrashlytics

class TransactionsInfo(context: Context, transactions: Transactions) : Dialog(context) {

    private val mTransactions = transactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val mBinding: TransDialogInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.trans_dialog_info, null, false
        )
        setContentView(mBinding.root)
        mBinding.mDialogTransInfo = mTransactions
        mBinding.btnOkDialogTrans = this
        setCancelable(false)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBinding.btnOk.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.black)
                mBinding.btnOk.setTextColor(context.getColor(R.color.white))
            } else {
                mBinding.btnOk.setBackgroundColor(context.resources.getColor(R.color.black))
                mBinding.btnOk.setTextColor(context.resources.getColor(R.color.white))
            }
        }
    }
}