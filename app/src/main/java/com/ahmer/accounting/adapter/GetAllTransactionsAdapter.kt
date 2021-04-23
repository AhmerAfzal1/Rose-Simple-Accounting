package com.ahmer.accounting.adapter

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.model.Transactions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GetAllTransactionsAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<GetAllTransactionsAdapter.TransactionsViewHolder>() {

    private val mContext = context
    private val mCursor = cursor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_add_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        mCursor.moveToPosition(position)
        val transaction = Transactions().apply {
            userId =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(Constants.TranColumn.USER_ID))
            date =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DATE))
            description =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DESCRIPTION))
            credit =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREDIT))
            debit =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DEBIT))
            balance =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.BALANCE))
            created =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREATED_ON))
            modified =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.LAST_MODIFIED))
        }
        holder.bindView(transaction)
        holder.cvTransactionEntry.setOnClickListener {
            showTransInfoDialog(mContext, transaction)
        }
    }

    override fun getItemCount(): Int {
        return if (mCursor.isClosed) {
            0
        } else {
            mCursor.count
        }
    }

    private fun showTransInfoDialog(context: Context, transactions: Transactions) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.transactions_info_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            val tvTransId = dialog.findViewById<TextView>(R.id.dialogTransID)
            val tvTransCreated = dialog.findViewById<TextView>(R.id.dialogTransCreated)
            val tvTransModified = dialog.findViewById<TextView>(R.id.dialogTransModified)
            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
            tvTransId.text = ""
            tvTransCreated.text = transactions.created
            tvTransModified.text = transactions.modified
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cvTransactionEntry: MaterialCardView = itemView.findViewById(R.id.cvTransactionEntry)

        fun bindView(transactions: Transactions) {
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
            val tvDeb = itemView.findViewById<TextView>(R.id.tvDebit)
            val tvCre = itemView.findViewById<TextView>(R.id.tvCredit)

            try {
                val sdf = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.UK)
                val date: Date = sdf.parse(transactions.date)
                val simpleDateFormat = SimpleDateFormat("dd-MM-yy", Locale.UK)
                tvDate.text = simpleDateFormat.format(date)
            } catch (pe: ParseException) {
                Log.e(Constants.LOG_TAG, pe.message, pe)
                FirebaseCrashlytics.getInstance().recordException(pe)
            }

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
        }
    }
}
