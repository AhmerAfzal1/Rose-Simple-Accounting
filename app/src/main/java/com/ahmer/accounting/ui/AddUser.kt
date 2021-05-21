package com.ahmer.accounting.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.UserProfileAddDataBinding
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyAds
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.button.MaterialButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

class AddUser : AppCompatActivity() {

    private lateinit var mBinding: UserProfileAddDataBinding
    private val mUserProfile = UserProfile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.user_profile_add_data)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        mBinding.mAddUserProfileActivity = this
        mBinding.mAddUserProfile = mUserProfile

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.toolbar.overflowIcon?.setTint(Color.WHITE)
        }

        mBinding.inputLayoutName.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding.inputLayoutName, InputMethodManager.SHOW_IMPLICIT)

        mBinding.btnToggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            mUserProfile.gender = checkedButton.text.toString()
            Log.v(Constants.LOG_TAG, "TypeGender: $mUserProfile.gender")
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (mUserProfile.gender) {
                        "Male" -> {
                            mBinding.btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                            mBinding.btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            mBinding.btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                        }
                        "Female" -> {
                            mBinding.btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            mBinding.btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            mBinding.btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                        }
                        else -> {
                            mBinding.btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            mBinding.btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            mBinding.btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                        }
                    }
                } else {
                    when (mUserProfile.gender) {
                        "Male" -> {
                            mBinding.btnMale.setBackgroundResource(R.color.black)
                            mBinding.btnFemale.setBackgroundResource(android.R.color.transparent)
                            mBinding.btnUnknown.setBackgroundResource(android.R.color.transparent)
                        }
                        "Female" -> {
                            mBinding.btnMale.setBackgroundResource(android.R.color.transparent)
                            mBinding.btnUnknown.setBackgroundResource(android.R.color.transparent)
                            mBinding.btnFemale.setBackgroundResource(R.color.black)
                        }
                        else -> {
                            mBinding.btnMale.setBackgroundResource(android.R.color.transparent)
                            mBinding.btnFemale.setBackgroundResource(android.R.color.transparent)
                            mBinding.btnUnknown.setBackgroundResource(R.color.black)
                        }
                    }
                }
            }
        }
        mUserProfile.created = HelperFunctions.getDateTime()
        MyAds.loadInterstitialAd(this)
    }

    fun saveData() {
        var isSuccessfullyAdded = false

        when {
            mUserProfile.name.trim().isEmpty() -> {
                ToastUtils.showLong(getString(R.string.toast_enter_name))
            }
            mUserProfile.gender.trim().isEmpty() -> {
                ToastUtils.showLong(getString(R.string.toast_select_gender))
            }
            else -> {
                val myDatabaseHelper = MyDatabaseHelper()
                isSuccessfullyAdded = myDatabaseHelper.insertUserProfileData(mUserProfile)
                /*
                Log.v(Constants.LOG_TAG, "Data Saved")
                Log.v(Constants.LOG_TAG, "Name: ${userProfile.name}")
                Log.v(Constants.LOG_TAG, "Gender: ${userProfile.gender}")
                Log.v(Constants.LOG_TAG, "Address: ${userProfile.address}")
                Log.v(Constants.LOG_TAG, "City: ${userProfile.city}")
                Log.v(Constants.LOG_TAG, "Phone1: ${userProfile.phone1}")
                Log.v(Constants.LOG_TAG, "Phone2: ${userProfile.phone2}")
                Log.v(Constants.LOG_TAG, "Email: ${userProfile.email}")
                Log.v(Constants.LOG_TAG, "Comments: ${userProfile.comment}")
                Log.v(Constants.LOG_TAG, "Created: ${userProfile.created}")
                Log.v(Constants.LOG_TAG, "Modified: ${userProfile.modified}")
                */
            }
        }

        if (isSuccessfullyAdded) {
            ToastUtils.showShort(getString(R.string.toast_record_saved))
            Thread.sleep(200)
            finish()
        }
    }
}
