package com.ahmer.accounting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = MyDatabaseHelper(applicationContext)
        /*
        db.insertData(
            "Ahmer Afzal", "Male", "Ward No. 16, Street No. 01, " +
                    "House No. 548, Darbar Road, Ghareeb Mohallah, Hasilpur, (District) Bahawalpur.",
            "Hasilpur", "03023339589", "03002039589", ""
        )*/
        db.getData()
    }
}