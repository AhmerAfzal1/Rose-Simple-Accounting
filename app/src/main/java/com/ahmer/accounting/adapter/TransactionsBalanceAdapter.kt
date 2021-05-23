package com.ahmer.accounting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.TransContainerBalanceBinding
import com.ahmer.accounting.model.TransactionsBalance

class TransactionsBalanceAdapter(list: ArrayList<TransactionsBalance>) :
    RecyclerView.Adapter<TransactionsBalanceAdapter.BalViewHolder>() {

    private val mList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalViewHolder {
        val mBinding: TransContainerBalanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.trans_container_balance,
            parent,
            false
        )
        return BalViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: BalViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class BalViewHolder(binding: TransContainerBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val mBinding = binding

        fun bind(transactionsBalance: TransactionsBalance) {
            mBinding.mAccountTypeModel = transactionsBalance
            mBinding.executePendingBindings()
        }
    }
}