package com.ahmer.accounting.adapter

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

class TransactionsAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

    private var mCanStart = true
    private val mContext = context
    private val mCursor = cursor
    private var mSelectedIds = ArrayList<Int>()
    private val mTransactions = ArrayList<Transactions>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_add_container, parent, false)
        return TransactionsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        mCursor.moveToPosition(position)
        val transaction = Transactions().apply {
            transId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID))
            userId =
                mCursor.getLong(mCursor.getColumnIndexOrThrow(Constants.TranColumn.USER_ID))
            date =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DATE))
            description =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DESCRIPTION))
            credit =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREDIT))
            debit =
                mCursor.getDouble(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DEBIT))
            isDebit =
                HelperFunctions.checkBoolean(mCursor.getInt(mCursor.getColumnIndexOrThrow(Constants.TranColumn.IS_DEBIT)))
            created =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.CREATED_ON))
            modified =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.LAST_MODIFIED))
        }
        holder.bindView(transaction)
        addTransList(transaction)
        holder.cvTransactionEntry.setOnClickListener {
            if (mCanStart) {
                showDropDownDialog(mContext, transaction)
            }
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

    fun setCanStart(start: Boolean) {
        mCanStart = start
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

    private fun showTransInfoDialog(context: Context, trans: Transactions) {
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
            tvTransId.text = trans.transId.toString()
            tvTransCreated.text = trans.created
            tvTransModified.text = trans.modified
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun showTransEditDialog(context: Context, trans: Transactions) {
        try {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.transactions_add_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            var isSuccessfullyUpdated = false
            val inputDate = dialog.findViewById<TextInputLayout>(R.id.textInputLayoutDate)
            val toggle = dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
            val inputAmount = dialog.findViewById<TextInputLayout>(R.id.textInputLayoutAmount)
            val inputDescription =
                dialog.findViewById<TextInputLayout>(R.id.textInputLayoutDescription)
            val addTransaction = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
            val cancelTransaction = dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

            val typeAmount: String = if (trans.credit == 0.toDouble()) {
                context.getString(R.string.debit_minus)
            } else {
                context.getString(R.string.credit_plus)
            }
            if (typeAmount == context.getString(R.string.debit_minus)) {
                toggle.check(R.id.toggleBtnDebit)
                inputAmount.editText?.setText(trans.debit.toString())
            } else {
                toggle.check(R.id.toggleBtnCredit)
                inputAmount.editText?.setText(trans.credit.toString())
            }
            inputDate.editText?.setText(HelperFunctions.getDateTime())
            inputDescription.editText?.setText(trans.description)
            addTransaction.text = context.getString(R.string.update_transaction)
            addTransaction.setOnClickListener {
                val myDatabaseHelper = MyDatabaseHelper(context)
                val newAmount: Double = inputAmount.editText?.text.toString().trim().toDouble()
                when {
                    newAmount == 0.toDouble() -> {
                        ToastUtils.showLong(context.getString(R.string.enter_the_amount))
                    }
                    typeAmount.isEmpty() -> {
                        ToastUtils.showLong(context.getString(R.string.select_type_amount))
                    }
                    inputDescription.toString().trim().isEmpty() -> {
                        ToastUtils.showLong(context.getString(R.string.enter_transaction_description))
                    }
                    else -> {
                        val transContentValues = Transactions().apply {
                            userId = trans.userId
                            credit = 0.toDouble()
                            debit = 0.toDouble()
                            if (typeAmount == context.getString(R.string.credit_plus)) {
                                credit = newAmount
                                isDebit = false
                            }
                            if (typeAmount == context.getString(R.string.debit_minus)) {
                                debit = newAmount
                                isDebit = true
                            }
                            date = inputDate.editText?.text.toString()
                            description = inputDescription.editText?.text.toString()
                            modified = HelperFunctions.getDateTime()
                        }
                        isSuccessfullyUpdated =
                            myDatabaseHelper.updateTransactions(trans.transId, transContentValues)
                    }
                }
                if (isSuccessfullyUpdated) {
                    ToastUtils.showShort(context.getString(R.string.transaction_updated_successfully))
                    Thread.sleep(200)
                    dialog.dismiss()
                }
            }
            cancelTransaction.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun showDropDownDialog(context: Context, trans: Transactions) {
        try {
            val dialogBuilder = MaterialAlertDialogBuilder(context)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }.create()
            val adapter =
                ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1)
            adapter.addAll(
                context.getString(R.string.edit),
                context.getString(R.string.info),
                context.getString(R.string.delete)
            )
            dialogBuilder.setAdapter(adapter) { dialog, which ->
                when (which) {
                    0 -> {
                        showTransEditDialog(context, trans)
                    }
                    1 -> {
                        showTransInfoDialog(context, trans)
                    }
                    2 -> {
                        HelperFunctions.confirmTransDelete(context, trans.transId)
                    }
                }
                dialog.dismiss()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(mContext.getColor(R.color.black))
                } else {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(mContext.resources.getColor(R.color.black))
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cvTransactionEntry: MaterialCardView = itemView.findViewById(R.id.cvTransactionEntry)

        fun bindView(trans: Transactions) {
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
            val tvDeb = itemView.findViewById<TextView>(R.id.tvDebit)
            val tvCre = itemView.findViewById<TextView>(R.id.tvCredit)
            tvDate.text = HelperFunctions.convertDateTimeShortFormat(trans.date)
            tvDesc.text = trans.description
            if (trans.debit == 0.toDouble()) {
                tvDeb.text = ""
            } else {
                tvDeb.text = HelperFunctions.getRoundedValue(trans.debit)
            }
            if (trans.credit == 0.toDouble()) {
                tvCre.text = ""
            } else {
                tvCre.text = HelperFunctions.getRoundedValue(trans.credit)
            }
        }
    }
}
