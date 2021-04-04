package com.ahmer.accounting

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.Constants.Companion.LOG_TAG
import com.ahmer.accounting.model.CustomerProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class AddCustomerData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer_data)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }
        val customerName = findViewById<TextInputLayout>(R.id.inputLayoutName)
        val customerGender = findViewById<RadioGroup>(R.id.rgGender)
        val customerAddress = findViewById<TextInputLayout>(R.id.inputLayoutAddress)
        val customerCity = findViewById<TextInputLayout>(R.id.inputLayoutCity)
        val customerPhone1 = findViewById<TextInputLayout>(R.id.inputLayoutPhone1)
        val customerPhone2 = findViewById<TextInputLayout>(R.id.inputLayoutPhone2)
        val customerPhone3 = findViewById<TextInputLayout>(R.id.inputLayoutPhone3)
        val customerComments = findViewById<TextInputLayout>(R.id.inputLayoutComments)
        var typeGender = ""
        customerGender.setOnCheckedChangeListener { _, checkedId ->
            val rbGender = findViewById<RadioButton>(checkedId)
            typeGender = rbGender.text.toString()
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_btn_save -> {
                    val customerProfile = CustomerProfile().apply {
                        this.name = customerName.editText?.text.toString().trim()
                        this.gender = typeGender
                        this.address = customerAddress.editText?.text.toString().trim()
                        this.city = customerCity.editText?.text.toString().trim()
                        this.phone1 = customerPhone1.editText?.text.toString().trim()
                        this.phone2 = customerPhone2.editText?.text.toString().trim()
                        this.phone3 = customerPhone3.editText?.text.toString().trim()
                        this.comment = customerComments.editText?.text.toString().trim()
                    }
                    try {
                        val myDatabaseHelper = MyDatabaseHelper(this)
                        myDatabaseHelper.insertCustomerProfileData(
                            name = customerProfile.name,
                            gender = customerProfile.gender,
                            address = customerProfile.address,
                            city = customerProfile.city,
                            phone1 = customerProfile.phone1,
                            phone2 = customerProfile.phone2,
                            phone3 = customerProfile.phone3,
                            comments = customerProfile.comment
                        )
                    } catch (e: SQLiteException) {
                        Log.v(LOG_TAG, e.printStackTrace().toString())
                    }
                    Log.v(LOG_TAG, "Data Saved")
                    Log.v(LOG_TAG, "Name: ${customerProfile.name}")
                    Log.v(LOG_TAG, "Gender: ${customerProfile.gender}")
                    Log.v(LOG_TAG, "Address: ${customerProfile.address}")
                    Log.v(LOG_TAG, "City: ${customerProfile.city}")
                    Log.v(LOG_TAG, "Phone1: ${customerProfile.phone1}")
                    Log.v(LOG_TAG, "Phone2: ${customerProfile.phone2}")
                    Log.v(LOG_TAG, "Phone3: ${customerProfile.phone3}")
                    Log.v(LOG_TAG, "Comment: ${customerProfile.comment}")
                    Toast.makeText(
                        this,
                        "Record successfully saved!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            true
        }
    }
}
