package com.ahmer.accounting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.TransactionsInfoDialogBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

class TransactionsEdit(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val mBinding: TransactionsInfoDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.transactions_add_dialog, null, false
        )
        setContentView(mBinding.root)
    }
}