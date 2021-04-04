package com.ahmer.accounting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.Constants.Companion.LOG_TAG
import com.ahmer.accounting.model.CustomerProfile
import com.google.android.material.appbar.MaterialToolbar

class GetCustomerData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_customer_data)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        val myDatabaseHelper = MyDatabaseHelper(this)
        val adapter = CustomerDataAdapter(this, myDatabaseHelper.getCustomerProfileData())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}

class CustomerDataAdapter(
    private val context: Context,
    private val customersList: ArrayList<CustomerProfile>
) : RecyclerView.Adapter<CustomerDataHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerDataHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.get_customer_data_container, parent, false)
        return CustomerDataHolder(inflater)
    }

    override fun onBindViewHolder(holder: CustomerDataHolder, position: Int) {
        holder.bindItems(customersList[position])
        holder.layoutCustomerID.setOnClickListener {
            showDialogMoreInfo(position)
        }
    }

    override fun getItemCount(): Int {
        return customersList.size
    }

    private fun showDialogMoreInfo(position: Int) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.get_customer_data_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            val getID = dialog.findViewById<TextView>(R.id.dialogCustomerID)
            val getName = dialog.findViewById<TextView>(R.id.dialogCustomerName)
            val getGender = dialog.findViewById<TextView>(R.id.dialogCustomerGender)
            val getAddress = dialog.findViewById<TextView>(R.id.dialogCustomerAddress)
            val getCity = dialog.findViewById<TextView>(R.id.dialogCustomerCity)
            val getPhone1 = dialog.findViewById<TextView>(R.id.dialogCustomerPhone1)
            val getPhone2 = dialog.findViewById<TextView>(R.id.dialogCustomerPhone2)
            val getPhone3 = dialog.findViewById<TextView>(R.id.dialogCustomerPhone3)
            val getComments = dialog.findViewById<TextView>(R.id.dialogCustomerComments)
            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
            Log.v(LOG_TAG, "ID: " + customersList[position].id.toString())
            Log.v(LOG_TAG, "Name: " + customersList[position].name)
            Log.v(LOG_TAG, "Gender: " + customersList[position].gender)
            Log.v(LOG_TAG, "Address: " + customersList[position].address)
            Log.v(LOG_TAG, "Phone1: " + customersList[position].phone1)
            getID.text = customersList[position].id.toString()
            getName.text = customersList[position].name
            getGender.text = customersList[position].gender
            getAddress.text = customersList[position].address
            getCity.text = customersList[position].city
            getPhone1.text = customersList[position].phone1
            getPhone2.text = customersList[position].phone2
            getPhone3.text = customersList[position].phone3
            getComments.text = customersList[position].comment
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }
    }
}

class CustomerDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val layoutCustomerID: LinearLayout = itemView.findViewById(R.id.linearLayoutCustomerID)

    fun bindItems(customerProfile: CustomerProfile) {
        val customerID = itemView.findViewById<TextView>(R.id.tvGetCustomerID)
        val customerName = itemView.findViewById<TextView>(R.id.tvGetCustomerName)
        customerID.text = customerProfile.id.toString()
        customerName.text = customerProfile.name
    }
}