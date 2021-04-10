package com.ahmer.accounting.user

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class AddUserProfileData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_add_data)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }

        val userName = findViewById<TextInputLayout>(R.id.inputLayoutName)
        val userGender = findViewById<RadioGroup>(R.id.rgGender)
        val userAddress = findViewById<TextInputLayout>(R.id.inputLayoutAddress)
        val userCity = findViewById<TextInputLayout>(R.id.inputLayoutCity)
        val userPhone1 = findViewById<TextInputLayout>(R.id.inputLayoutPhone1)
        val userPhone2 = findViewById<TextInputLayout>(R.id.inputLayoutPhone2)
        val userEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val userComments = findViewById<TextInputLayout>(R.id.inputLayoutComments)
        var typeGender = ""
        userGender.setOnCheckedChangeListener { _, checkedId ->
            val rbGender = findViewById<RadioButton>(checkedId)
            typeGender = rbGender.text.toString()
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_btn_save -> {
                    var isSuccessfullyAdded = false
                    val userProfile = UserProfile().apply {
                        this.name = userName.editText?.text.toString().trim()
                        this.gender = typeGender
                        this.address = userAddress.editText?.text.toString().trim()
                        this.city = userCity.editText?.text.toString().trim()
                        this.phone1 = userPhone1.editText?.text.toString().trim()
                        this.phone2 = userPhone2.editText?.text.toString().trim()
                        this.email = userEmail.editText?.text.toString().trim()
                        this.comment = userComments.editText?.text.toString().trim()
                        this.created = HelperFunctions.getDateTime()
                    }

                    when {
                        userProfile.name.trim().isEmpty() -> {
                            HelperFunctions.makeToast(this, getString(R.string.toast_enter_name))
                        }
                        userProfile.gender.trim().isEmpty() -> {
                            HelperFunctions.makeToast(this, getString(R.string.toast_select_gender))
                        }
                        else -> {
                            val myDatabaseHelper = MyDatabaseHelper(this)
                            isSuccessfullyAdded =
                                myDatabaseHelper.insertUserProfileData(userProfile)

                            Log.v(LOG_TAG, "Data Saved")
                            Log.v(LOG_TAG, "Name: ${userProfile.name}")
                            Log.v(LOG_TAG, "Gender: ${userProfile.gender}")
                            Log.v(LOG_TAG, "Address: ${userProfile.address}")
                            Log.v(LOG_TAG, "City: ${userProfile.city}")
                            Log.v(LOG_TAG, "Phone1: ${userProfile.phone1}")
                            Log.v(LOG_TAG, "Phone2: ${userProfile.phone2}")
                            Log.v(LOG_TAG, "Email: ${userProfile.email}")
                            Log.v(LOG_TAG, "Comments: ${userProfile.comment}")
                            Log.v(LOG_TAG, "Created: ${userProfile.created}")
                            Log.v(LOG_TAG, "Modified: ${userProfile.modified}")
                        }
                    }

                    if (isSuccessfullyAdded) {
                        HelperFunctions.makeToast(this, getString(R.string.toast_record_saved))
                        Thread.sleep(200)
                        finish()
                    }
                }
            }
            true
        }
    }
}
