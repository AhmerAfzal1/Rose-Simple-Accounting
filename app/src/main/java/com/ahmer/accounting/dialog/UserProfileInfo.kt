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
import com.ahmer.accounting.databinding.UserDialogInfoBinding
import com.ahmer.accounting.model.UserProfile
import com.google.firebase.crashlytics.FirebaseCrashlytics

class UserProfileInfo(context: Context, userProfile: UserProfile) :
    Dialog(context, R.style.Theme_RoseSimpleAccounting_Dialog) {

    private val mUserProfile = userProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val mBinding: UserDialogInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.user_dialog_info, null, false
        )
        setContentView(mBinding.root)
        mBinding.mDialogUserProfile = mUserProfile
        mBinding.btnOkDialogUserProfile = this
        mBinding.executePendingBindings()
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