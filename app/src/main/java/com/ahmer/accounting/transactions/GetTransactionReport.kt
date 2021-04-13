package com.ahmer.accounting.transactions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar

class GetTransactionReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_rv)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        val mUserId = intent.getIntExtra("mUserIDPosition", -1)
        val mUserName = intent.getStringExtra("mUserNamePosition")
        Log.v(LOG_TAG, "ID: $mUserId, Name: $mUserName")

        val tvUserId = findViewById<TextView>(R.id.tvUserId)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvUserId.text = mUserId.toString()
        tvUserName.text = mUserName

        val recyclerView = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GetTransactionsStatementAdapter(this, mUserId)
    }
}

class GetTransactionsStatementAdapter(context: Context, mUserId: Int) :
    RecyclerView.Adapter<TransactionsViewHolder>() {

    private val mContext = context
    private val myDatabaseHelper = MyDatabaseHelper(mContext)
    private val mTransactionsList = myDatabaseHelper.getTransactionsByUserId(mUserId)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_statement_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bindView(mTransactionsList[position])
    }

    override fun getItemCount(): Int {
        return mTransactionsList.size
    }

}

class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(transactions: Transactions) {
        val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvDeb = itemView.findViewById<TextView>(R.id.tvDebit)
        val tvCre = itemView.findViewById<TextView>(R.id.tvCredit)
        val tvBal = itemView.findViewById<TextView>(R.id.tvBalance)
        tvDesc.text = transactions.description
        if (transactions.debit == 0.toDouble()) {
            tvDeb.text = ""
        } else {
            tvDeb.text = transactions.debit.toString()
        }
        if (transactions.credit == 0.toDouble()) {
            tvCre.text = ""
        } else {
            tvCre.text = transactions.credit.toString()
        }
        tvBal.text = transactions.balance.toString()
    }
}
