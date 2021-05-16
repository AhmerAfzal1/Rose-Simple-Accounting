package com.ahmer.accounting.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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

class EditUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_add_data)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_toolbar_edit_user)
        toolbar.setOnClickListener {
            finish()
        }

        val userName = findViewById<TextInputLayout>(R.id.inputLayoutName)
        val toggleGroupGender = findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupGender)
        val userAddress = findViewById<TextInputLayout>(R.id.inputLayoutAddress)
        val userCity = findViewById<TextInputLayout>(R.id.inputLayoutCity)
        val userPhone1 = findViewById<TextInputLayout>(R.id.inputLayoutPhone1)
        val userPhone2 = findViewById<TextInputLayout>(R.id.inputLayoutPhone2)
        val userEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val userComments = findViewById<TextInputLayout>(R.id.inputLayoutComments)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveUserData)

        var typeGender = ""
        toggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = findViewById<MaterialButton>(checkedId)
            typeGender = checkedButton.text.toString()
        }

        val id = intent.getLongExtra("mID", -1)
        val name = intent.getStringExtra("mName")
        val gender = intent.getStringExtra("mGender")
        val address = intent.getStringExtra("mAddress")
        val city = intent.getStringExtra("mCity")
        val phone1 = intent.getStringExtra("mPhone1")
        val phone2 = intent.getStringExtra("mPhone2")
        val email = intent.getStringExtra("mEmail")
        val comment = intent.getStringExtra("mComments")

        userName.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                userName.boxStrokeColor = getColor(R.color.black)
                userAddress.boxStrokeColor = getColor(R.color.black)
                userCity.boxStrokeColor = getColor(R.color.black)
                userPhone1.boxStrokeColor = getColor(R.color.black)
                userPhone2.boxStrokeColor = getColor(R.color.black)
                userEmail.boxStrokeColor = getColor(R.color.black)
                userComments.boxStrokeColor = getColor(R.color.black)
            } else {
                userName.boxStrokeColor = resources.getColor(R.color.black)
                userAddress.boxStrokeColor = resources.getColor(R.color.black)
                userCity.boxStrokeColor = resources.getColor(R.color.black)
                userPhone1.boxStrokeColor = resources.getColor(R.color.black)
                userPhone2.boxStrokeColor = resources.getColor(R.color.black)
                userEmail.boxStrokeColor = resources.getColor(R.color.black)
                userComments.boxStrokeColor = resources.getColor(R.color.black)
            }
        }

        userName.editText?.setText(name.toString())
        Log.v(Constants.LOG_TAG, "Gender type: $gender")
        when (gender) {
            "Male" -> {
                toggleGroupGender.check(R.id.btnMale)
            }
            "Female" -> {
                toggleGroupGender.check(R.id.btnFemale)
            }
            else -> {
                toggleGroupGender.check(R.id.btnUnknown)
            }
        }
        userAddress.editText?.setText(address.toString())
        userCity.editText?.setText(city.toString())
        userPhone1.editText?.setText(phone1.toString())
        userPhone2.editText?.setText(phone2.toString())
        userEmail.editText?.setText(email.toString())
        userComments.editText?.setText(comment.toString())

        btnSave.setOnClickListener {
            var isSuccessfullyUpdated = false
            val userProfile = UserProfile().apply {
                this.name = userName.editText?.text.toString().trim()
                this.gender = typeGender
                this.address = userAddress.editText?.text.toString().trim()
                this.city = userCity.editText?.text.toString().trim()
                this.phone1 = userPhone1.editText?.text.toString().trim()
                this.phone2 = userPhone2.editText?.text.toString().trim()
                this.email = userEmail.editText?.text.toString().trim()
                this.comment = userComments.editText?.text.toString().trim()
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
                    val myDatabaseHelper = MyDatabaseHelper(it.context)
                    isSuccessfullyUpdated = myDatabaseHelper.updateUserProfileData(userProfile, id)
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
}