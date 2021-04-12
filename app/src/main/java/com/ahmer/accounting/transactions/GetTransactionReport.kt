package com.ahmer.accounting.transactions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
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

        val mUserId = intent.getIntExtra("mUserID", -1)
        val transactionsRecord = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        transactionsRecord.layoutManager = LinearLayoutManager(this)
        transactionsRecord.adapter = GetTransactionsStatementAdapter(this, mUserId)
    }
}

class GetTransactionsStatementAdapter(context: Context, mUserId: Int) :
    RecyclerView.Adapter<TransactionsViewHolder>() {

    private val mContext = context
    private val myDatabaseHelper = MyDatabaseHelper(mContext)
    private val transactionsList = myDatabaseHelper.getTransactionsByUserId(mUserId)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_statement_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bindView(transactionsList[position])
    }

    override fun getItemCount(): Int {
        return transactionsList.size
    }

}

class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(transactions: Transactions) {
        val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvDeb = itemView.findViewById<TextView>(R.id.tvDebit)
        val tvCre = itemView.findViewById<TextView>(R.id.tvCredit)
        val tvBal = itemView.findViewById<TextView>(R.id.tvBalance)
        tvDesc.text = transactions.description
        tvDeb.text = transactions.debit.toString()
        tvCre.text = transactions.credit.toString()
        tvBal.text = transactions.balance.toString()
    }
}
