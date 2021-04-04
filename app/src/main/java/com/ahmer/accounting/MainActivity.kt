package com.ahmer.accounting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val databaseHelper = MyDatabaseHelper(applicationContext)
        databaseHelper.getCustomerProfileData()

        val addRecord = findViewById<MaterialButton>(R.id.addCustomerProfile)
        val getRecord = findViewById<MaterialButton>(R.id.getCustomerRecord)

        addRecord.setOnClickListener {
            val intent = Intent(this, AddCustomerData::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            startActivity(intent)
        }

        getRecord.setOnClickListener {
            val intent = Intent(this, GetCustomerData::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            startActivity(intent)
        }
    }
}