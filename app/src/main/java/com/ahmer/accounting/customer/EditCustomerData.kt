package com.ahmer.accounting.customer

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.CustomerProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class EditCustomerData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_data)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_toolbar_edit_customer)
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
        val customerEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val customerComments = findViewById<TextInputLayout>(R.id.inputLayoutComments)
        var typeGender = ""
        customerGender.setOnCheckedChangeListener { _, checkedId ->
            val rbGender = findViewById<RadioButton>(checkedId)
            typeGender = rbGender.text.toString()
        }

        val id = intent.getIntExtra("mID", -1)
        val name = intent.getStringExtra("mName")
        val gender = intent.getStringExtra("mGender")
        val address = intent.getStringExtra("mAddress")
        val city = intent.getStringExtra("mCity")
        val phone1 = intent.getStringExtra("mPhone1")
        val phone2 = intent.getStringExtra("mPhone2")
        val phone3 = intent.getStringExtra("mPhone3")
        val email = intent.getStringExtra("mEmail")
        val comment = intent.getStringExtra("mComments")

        customerName.editText?.setText(name.toString())
        when (gender) {
            "Male" -> {
                customerGender.check(R.id.rbMale)
            }
            "Female" -> {
                customerGender.check(R.id.rbFemale)
            }
            else -> {
                customerGender.check(R.id.rbUnknown)
            }
        }
        customerAddress.editText?.setText(address.toString())
        customerCity.editText?.setText(city.toString())
        customerPhone1.editText?.setText(phone1.toString())
        customerPhone2.editText?.setText(phone2.toString())
        customerPhone3.editText?.setText(phone3.toString())
        customerEmail.editText?.setText(email.toString())
        customerComments.editText?.setText(comment.toString())

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_btn_save -> {
                    try {
                        val customerProfile = CustomerProfile().apply {
                            this.name = customerName.editText?.text.toString().trim()
                            this.gender = typeGender
                            this.address = customerAddress.editText?.text.toString().trim()
                            this.city = customerCity.editText?.text.toString().trim()
                            this.phone1 = customerPhone1.editText?.text.toString().trim()
                            this.phone2 = customerPhone2.editText?.text.toString().trim()
                            this.phone3 = customerPhone3.editText?.text.toString().trim()
                            this.email = customerEmail.editText?.text.toString().trim()
                            this.comment = customerComments.editText?.text.toString().trim()
                        }

                        val myDatabaseHelper = MyDatabaseHelper(this)
                        myDatabaseHelper.updateCustomerProfileData(customerProfile, id)

                        Log.v(LOG_TAG, "Updated Record")
                        Log.v(LOG_TAG, "Name: ${customerProfile.name}")
                        Log.v(LOG_TAG, "Gender: ${customerProfile.gender}")
                        Log.v(LOG_TAG, "Address: ${customerProfile.address}")
                        Log.v(LOG_TAG, "City: ${customerProfile.city}")
                        Log.v(LOG_TAG, "Phone1: ${customerProfile.phone1}")
                        Log.v(LOG_TAG, "Phone2: ${customerProfile.phone2}")
                        Log.v(LOG_TAG, "Phone3: ${customerProfile.phone3}")
                        Log.v(LOG_TAG, "Email: ${customerProfile.email}")
                        Log.v(LOG_TAG, "Comments: ${customerProfile.comment}")
                    } catch (e: SQLiteException) {
                        Log.v(LOG_TAG, e.printStackTrace().toString())
                    }
                    Toast.makeText(
                        this,
                        "Record successfully updated!",
                        Toast.LENGTH_LONG
                    ).show()
                    Thread.sleep(300)
                    finish()
                }
            }
            true
        }
    }
}
