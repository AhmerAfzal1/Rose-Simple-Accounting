package com.ahmer.accounting.customer

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.CustomerProfile
import com.google.android.material.appbar.MaterialToolbar

class GetCustomerData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_records)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_customer_record)
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
            .inflate(R.layout.customer_data_container, parent, false)
        return CustomerDataHolder(inflater)
    }

    override fun onBindViewHolder(holder: CustomerDataHolder, position: Int) {
        holder.bindItems(customersList[position])
        holder.layoutCustomerID.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.layoutCustomerName.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.layoutEditProfile.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.tvEditButton.setOnClickListener {
            val intent = Intent(context, EditCustomerData::class.java).apply {
                putExtra("mID", customersList[position].id)
                putExtra("mName", customersList[position].name)
                putExtra("mGender", customersList[position].gender)
                putExtra("mAddress", customersList[position].address)
                putExtra("mCity", customersList[position].city)
                putExtra("mPhone1", customersList[position].phone1)
                putExtra("mPhone2", customersList[position].phone2)
                putExtra("mPhone3", customersList[position].phone3)
                putExtra("mEmail", customersList[position].email)
                putExtra("mComments", customersList[position].comment)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return customersList.size
    }

    private fun showDialogMoreInfo(position: Int) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.customer_data_dialog)
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
            val getEmail = dialog.findViewById<TextView>(R.id.dialogCustomerEmail)
            val getComments = dialog.findViewById<TextView>(R.id.dialogCustomerComments)
            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
            Log.v(LOG_TAG, "Dialog ID: ${customersList[position].id}")
            Log.v(LOG_TAG, "Dialog Name: ${customersList[position].name}")
            Log.v(LOG_TAG, "Dialog Gender: ${customersList[position].gender}")
            Log.v(LOG_TAG, "Dialog Address: ${customersList[position].address}")
            Log.v(LOG_TAG, "Dialog Phone1: ${customersList[position].phone1}")
            Log.v(LOG_TAG, "Dialog Phone2: ${customersList[position].phone2}")
            Log.v(LOG_TAG, "Dialog Phone3: ${customersList[position].phone3}")
            Log.v(LOG_TAG, "Dialog Email: ${customersList[position].email}")
            Log.v(LOG_TAG, "Dialog Comments: ${customersList[position].comment}")
            getID.text = customersList[position].id.toString()
            getName.text = customersList[position].name
            getGender.text = customersList[position].gender
            getAddress.text = customersList[position].address
            getCity.text = customersList[position].city
            getPhone1.text = customersList[position].phone1
            getPhone2.text = customersList[position].phone2
            getPhone3.text = customersList[position].phone3
            getEmail.text = customersList[position].email
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
    val layoutCustomerName: LinearLayout = itemView.findViewById(R.id.linearLayoutCustomerName)
    val layoutEditProfile: RelativeLayout = itemView.findViewById(R.id.relativeLayoutEditProfile)
    val tvEditButton: TextView = itemView.findViewById(R.id.tvBtnEdit)

    fun bindItems(customerProfile: CustomerProfile) {
        val customerID = itemView.findViewById<TextView>(R.id.tvGetCustomerID)
        val customerName = itemView.findViewById<TextView>(R.id.tvGetCustomerName)
        customerID.text = customerProfile.id.toString()
        customerName.text = customerProfile.name
    }
}