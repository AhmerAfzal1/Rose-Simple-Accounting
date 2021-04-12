package com.ahmer.accounting.transactions

import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView

class UserIdTransactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_records)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        toolbar.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TransactionsAdapter(this)
    }
}

class TransactionsAdapter(context: Context) : RecyclerView.Adapter<TransViewHolder>() {

    private val mContext = context
    private val myDatabaseHelper = MyDatabaseHelper(mContext)
    private val mUserProfileList = myDatabaseHelper.getUserProfileData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
                .inflate(R.layout.transactions_user_id_container, parent, false)
        return TransViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        // Position + 1 due to position start from 0
        val mUserPreviousBalance = myDatabaseHelper.getPreviousBalanceByUserId(position + 1)
        holder.bindItems(mUserProfileList[position], mUserPreviousBalance)
        holder.cardView.setOnClickListener {
            val intent = Intent(mContext, GetTransactionReport::class.java).apply {
                putExtra("mUserIDPosition", mUserProfileList[position].id)
                putExtra("mUserNamePosition", mUserProfileList[position].name)
                Log.v(LOG_TAG, "Position id: ${mUserProfileList[position].id}")
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mUserProfileList.size
    }
}

class TransViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cardView: MaterialCardView = itemView.findViewById(R.id.cardViewMain)

    fun bindItems(userProfile: UserProfile, mUserLastBalance: Double) {
        val userID: TextView = itemView.findViewById(R.id.tvGetUserID)
        val userName: TextView = itemView.findViewById(R.id.tvGetUserName)
        val userBalance: TextView = itemView.findViewById(R.id.tvGetBalance)
        userID.text = userProfile.id.toString()
        userName.text = userProfile.name
        userBalance.text = mUserLastBalance.toString()
    }
}
