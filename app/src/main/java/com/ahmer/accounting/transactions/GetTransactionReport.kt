package com.ahmer.accounting.transactions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.CustomerProfile
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar

class GetTransactionReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_records)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        val myDatabaseHelper = MyDatabaseHelper(this)
        val transactionsRecord = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        transactionsRecord.layoutManager = LinearLayoutManager(this)
        val customerProfile = myDatabaseHelper.getCustomerProfileData()
        val transactions = myDatabaseHelper.getTransactions()
        transactionsRecord.adapter = TransactionsAdapter(this, customerProfile, transactions)
    }
}

class TransactionsAdapter(
    private val context: Context,
    private val customerProfileList: ArrayList<CustomerProfile>,
    private val transactionsList: ArrayList<Transactions>
) : RecyclerView.Adapter<TransactionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bindView(customerProfileList[position], transactionsList[position])
        holder.linearLayoutTransactionReport.setOnClickListener {
            Toast.makeText(context, "More details are not yet applied", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return transactionsList.size
    }

}

class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val linearLayoutTransactionReport: LinearLayout =
        itemView.findViewById(R.id.linearLayoutTransactionReport)

    fun bindView(customerProfile: CustomerProfile, transactions: Transactions) {
        val tvId = itemView.findViewById<TextView>(R.id.tvGetCustomerID)
        val tvName = itemView.findViewById<TextView>(R.id.tvGetCustomerName)
        val tvBalance = itemView.findViewById<TextView>(R.id.tvGetBalance)
        tvId.text = customerProfile.id.toString()
        tvName.text = customerProfile.name
        tvBalance.text = transactions.balance.toString()
    }
}
