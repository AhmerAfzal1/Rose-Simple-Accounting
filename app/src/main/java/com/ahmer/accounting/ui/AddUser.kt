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
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

class AddUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_add_data)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.overflowIcon?.setTint(Color.WHITE)
        }
        toolbar.setOnClickListener {
            finish()
        }

        val userName = findViewById<TextInputLayout>(R.id.inputLayoutName)
        val toggleGroupGender = findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupGender)
        val btnMale = findViewById<MaterialButton>(R.id.btnMale)
        val btnFemale = findViewById<MaterialButton>(R.id.btnFemale)
        val btnUnknown = findViewById<MaterialButton>(R.id.btnUnknown)
        val userAddress = findViewById<TextInputLayout>(R.id.inputLayoutAddress)
        val userCity = findViewById<TextInputLayout>(R.id.inputLayoutCity)
        val userPhone1 = findViewById<TextInputLayout>(R.id.inputLayoutPhone1)
        val userPhone2 = findViewById<TextInputLayout>(R.id.inputLayoutPhone2)
        val userEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val userComments = findViewById<TextInputLayout>(R.id.inputLayoutComments)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveUserData)

        userName.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT)

        var typeGender = ""
        toggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            typeGender = checkedButton.text.toString()
            Log.v(Constants.LOG_TAG, "TypeGender: $typeGender")
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (typeGender) {
                        "Male" -> {
                            btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                            btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                        }
                        "Female" -> {
                            btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                        }
                        else -> {
                            btnMale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            btnFemale.backgroundTintList =
                                ContextCompat.getColorStateList(this, android.R.color.transparent)
                            btnUnknown.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.black)
                        }
                    }
                } else {
                    when (typeGender) {
                        "Male" -> {
                            btnMale.setBackgroundResource(R.color.black)
                            btnFemale.setBackgroundResource(android.R.color.transparent)
                            btnUnknown.setBackgroundResource(android.R.color.transparent)
                        }
                        "Female" -> {
                            btnMale.setBackgroundResource(android.R.color.transparent)
                            btnUnknown.setBackgroundResource(android.R.color.transparent)
                            btnFemale.setBackgroundResource(R.color.black)
                        }
                        else -> {
                            btnMale.setBackgroundResource(android.R.color.transparent)
                            btnFemale.setBackgroundResource(android.R.color.transparent)
                            btnUnknown.setBackgroundResource(R.color.black)
                        }
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            var isSuccessfullyAdded = false
            val userProfile = UserProfile().apply {
                name = userName.editText?.text.toString().trim()
                gender = typeGender
                address = userAddress.editText?.text.toString().trim()
                city = userCity.editText?.text.toString().trim()
                phone1 = userPhone1.editText?.text.toString().trim()
                phone2 = userPhone2.editText?.text.toString().trim()
                email = userEmail.editText?.text.toString().trim()
                comment = userComments.editText?.text.toString().trim()
                created = HelperFunctions.getDateTime()
            }

            when {
                userProfile.name.trim().isEmpty() -> {
                    ToastUtils.showLong(getString(R.string.toast_enter_name))
                }
                userProfile.gender.trim().isEmpty() -> {
                    ToastUtils.showLong(getString(R.string.toast_select_gender))
                }
                else -> {
                    val myDatabaseHelper = MyDatabaseHelper(it.context)
                    isSuccessfullyAdded = myDatabaseHelper.insertUserProfileData(userProfile)
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
}
