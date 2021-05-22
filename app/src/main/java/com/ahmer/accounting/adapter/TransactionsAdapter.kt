package com.ahmer.accounting.adapter

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.TransContainerAddBinding
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDialogs
import com.ahmer.accounting.model.Transactions
import com.google.android.material.card.MaterialCardView

class TransactionsAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

    private val mContext = context
    private val mCursor = cursor
    private var mSelectedIds = ArrayList<Int>()
    private val mTransactions = ArrayList<Transactions>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val mBinding: TransContainerAddBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.trans_container_add,
            parent,
            false
        )
        return TransactionsViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        mCursor.moveToPosition(position)
        val transaction = Transactions().apply {
            transId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID))
            userId = mCursor.getLong(mCursor.getColumnIndexOrThrow(Constants.TranColumn.USER_ID))
            date = mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DATE))
            description =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DESCRIPTION))
            credit = mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREDIT))
            debit = mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DEBIT))
            isDebit =
                HelperFunctions.checkBoolean(mCursor.getInt(mCursor.getColumnIndexOrThrow(Constants.TranColumn.IS_DEBIT)))
            created =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREATED_ON))
            modified =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.LAST_MODIFIED))
            modifiedAccountType =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.LAST_MODIFIED_ACCOUNT_TYPE))
            modifiedValue =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.LAST_MODIFIED_VALUE))
        }
        holder.bindView(transaction)
        addTransList(transaction)
        holder.cvTransactionEntry.setOnClickListener {
            MyDialogs.showDropDownDialog(mContext, transaction)
        }
        if (mSelectedIds.contains(position)) {
            holder.itemView.setBackgroundResource(R.color.secondaryLightColor)
        } else {
            holder.itemView.setBackgroundResource(R.color.colorBgContainer)
        }
    }

    override fun getItemCount(): Int {
        return if (mCursor.isClosed) {
            0
        } else {
            mCursor.count
        }
    }

    private fun addTransList(transactions: Transactions) {
        mTransactions.add(transactions)
    }

    fun getTransList(): ArrayList<Transactions> {
        return mTransactions
    }

    fun addSelectedIds(ids: ArrayList<Int>) {
        mSelectedIds = ids
        notifyDataSetChanged()
    }

    fun removeSelectedIds(ids: Int) {
        mSelectedIds.remove(ids)
        notifyDataSetChanged()
    }

    class TransactionsViewHolder(binding: TransContainerAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val cvTransactionEntry: MaterialCardView = binding.cvTransactionEntry
        val mBinding = binding

        fun bindView(trans: Transactions) {
            mBinding.mTransactionModel = trans
        }
    }
}
