package com.ahmer.accounting.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.UserAddBinding
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyAds
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.button.MaterialButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.NetworkUtils
import io.ahmer.utils.utilcode.ToastUtils

class EditUser : AppCompatActivity() {

    private lateinit var mBinding: UserAddBinding
    private val mUserProfile = UserProfile()
    private var mIntentId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.user_add)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.toolbarAddUserProfile.overflowIcon?.setTint(Color.WHITE)
        }

        mIntentId = intent.getLongExtra("mID", -1)
        val intentName = intent.getStringExtra("mName")
        val intentGender = intent.getStringExtra("mGender")
        val intentAddress = intent.getStringExtra("mAddress")
        val intentCity = intent.getStringExtra("mCity")
        val intentPhone1 = intent.getStringExtra("mPhone1")
        val intentPhone2 = intent.getStringExtra("mPhone2")
        val intentEmail = intent.getStringExtra("mEmail")
        val intentComment = intent.getStringExtra("mComments")

        mBinding.mAddUserProfile = mUserProfile
        mBinding.isAddOrEdit = false
        mBinding.mEditUserProfileActivity = this

        mBinding.inputLayoutName.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding.inputLayoutName, InputMethodManager.SHOW_IMPLICIT)

        mBinding.btnToggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            mUserProfile.gender = checkedButton.text.toString()
            Log.v(Constants.LOG_TAG, "GenderType: ${mUserProfile.gender}")
            /*if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
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
            }*/
        }

        mUserProfile.name = intentName!!
        Log.v(Constants.LOG_TAG, "Gender type: $intentGender")
        when (intentGender) {
            "Male" -> {
                mBinding.btnToggleGroupGender.check(R.id.btnMale)
            }
            "Female" -> {
                mBinding.btnToggleGroupGender.check(R.id.btnFemale)
            }
            else -> {
                mBinding.btnToggleGroupGender.check(R.id.btnUnknown)
            }
        }
        mUserProfile.address = intentAddress!!
        mUserProfile.city = intentCity!!
        mUserProfile.phone1 = intentPhone1!!
        mUserProfile.phone2 = intentPhone2!!
        mUserProfile.email = intentEmail!!
        mUserProfile.comment = intentComment!!

        if (NetworkUtils.isConnected()) {
            MyAds.loadInterstitialAd(this)
        }
    }

    fun saveData() {
        var isSuccessfullyUpdated = false
        val userProfile = UserProfile().apply {
            this.name = mUserProfile.name
            this.gender = mUserProfile.gender
            this.address = mUserProfile.address
            this.city = mUserProfile.city
            this.phone1 = mUserProfile.phone1
            this.phone2 = mUserProfile.phone2
            this.email = mUserProfile.email
            this.comment = mUserProfile.comment
            this.modified = HelperFunctions.getDateTime()
        }

        when {
            userProfile.name.trim().isEmpty() -> {
                ToastUtils.showLong(getString(R.string.toast_enter_name))
            }
            userProfile.gender.trim().isEmpty() -> {
                ToastUtils.showLong(getString(R.string.toast_select_gender))
            }
            else -> {
                val myDatabaseHelper = MyDatabaseHelper()
                isSuccessfullyUpdated =
                    myDatabaseHelper.updateUserProfileData(userProfile, mIntentId)
                /*
                Log.v(Constants.LOG_TAG, "Updated Record")
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

        if (isSuccessfullyUpdated) {
            ToastUtils.showShort(getString(R.string.toast_record_updated))
            Thread.sleep(200)
            finish()
        }
    }
}
