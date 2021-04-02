package com.ahmer.accounting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val databaseHelper = MyDatabaseHelper(applicationContext)

        /*
        val customerProfile = CustomerProfile()
        customerProfile.name = "Ahmer Afzal"
        customerProfile.gender = "Male"
        customerProfile.address =
            "Ward No. 16, Street No. 01, House No. 548, Darbar Road, Ghareeb Mohallah, Hasilpur, " +
                    "(District) Bahawalpur."
        customerProfile.city = "Hasilpur"
        customerProfile.phone1 = "03023339589"
        customerProfile.phone2 = "03002039589"
        customerProfile.phone3 = ""

        databaseHelper.insertData(
            customerProfile.name,
            customerProfile.gender,
            customerProfile.address,
            customerProfile.city,
            customerProfile.phone1,
            customerProfile.phone2,
            customerProfile.phone3
        )
        */

        Thread.sleep(1500)
        databaseHelper.getData()
    }
}