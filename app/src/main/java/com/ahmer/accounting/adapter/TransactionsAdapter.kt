package com.ahmer.accounting.adapter

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class TransactionsAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

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
            transId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID))
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
            showDropDownDialog(mContext, transaction)
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
            tvTransId.text = transactions.transId.toString()
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
            val inputDate = dialog.findViewById<TextInputEditText>(R.id.inputDate)
            val toggle = dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
            val inputAmount = dialog.findViewById<TextInputEditText>(R.id.inputAmount)
            val inputDescription = dialog.findViewById<TextInputEditText>(R.id.inputDescription)
            val addTransaction = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
            val cancelTransaction = dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

            val typeAmount: String = if (trans.credit == 0.toDouble()) {
                context.getString(R.string.debit_minus)
            } else {
                context.getString(R.string.credit_plus)
            }
            if (typeAmount == context.getString(R.string.debit_minus)) {
                toggle.check(R.id.toggleBtnDebit)
                inputAmount.setText(trans.debit.toString())
            } else {
                toggle.check(R.id.toggleBtnCredit)
                inputAmount.setText(trans.credit.toString())
            }
            inputDate.setText(HelperFunctions.getDateTime())
            inputDescription.setText(trans.description)
            addTransaction.text = context.getString(R.string.update_transaction)
            addTransaction.setOnClickListener {
                val myDatabaseHelper = MyDatabaseHelper(context)
                val newAmount: Double = inputAmount.text.toString().trim().toDouble()
                when {
                    newAmount == 0.toDouble() -> {
                        HelperFunctions.makeToast(
                            context,
                            context.getString(R.string.enter_the_amount)
                        )
                    }
                    typeAmount.isEmpty() -> {
                        HelperFunctions.makeToast(
                            context,
                            context.getString(R.string.select_type_amount)
                        )
                    }
                    inputDescription.toString().trim().isEmpty() -> {
                        HelperFunctions.makeToast(
                            context,
                            context.getString(R.string.enter_transaction_description)
                        )
                    }
                    else -> {
                        val transContentValues = Transactions().apply {
                            userId = trans.userId
                            credit = 0.toDouble()
                            debit = 0.toDouble()
                            if (typeAmount == context.getString(R.string.credit_plus)) {
                                credit = newAmount
                                balance += newAmount
                            }
                            if (typeAmount == context.getString(R.string.debit_minus)) {
                                debit = newAmount
                                balance -= newAmount
                            }
                            date = inputDate.text.toString()
                            description = inputDescription.text.toString()
                            modified = HelperFunctions.getDateTime()
                        }
                        isSuccessfullyUpdated =
                            myDatabaseHelper.updateTransactions(trans.transId, transContentValues)
                    }
                }
                if (isSuccessfullyUpdated) {
                    HelperFunctions.makeToast(
                        context,
                        context.getString(R.string.transaction_updated_successfully)
                    )
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

    private fun showDropDownDialog(context: Context, transactions: Transactions) {
        try {
            val dialogBuilder = MaterialAlertDialogBuilder(context)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            val adapter =
                ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1)
            adapter.add("Edit")
            adapter.add("Info")
            adapter.add("Delete")
            dialogBuilder.setAdapter(adapter) { dialog, which ->
                when (which) {
                    0 -> {
                        showTransEditDialog(context, transactions)
                    }
                    1 -> {
                        showTransInfoDialog(context, transactions)
                    }
                    2 -> {
                        confirmTransDelete(context, transactions)
                    }
                }
                dialog.dismiss()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun confirmTransDelete(context: Context, transactions: Transactions) {
        try {
            var isDeleted = false
            val myDatabaseHelper = MyDatabaseHelper(context)
            val alertBuilder = MaterialAlertDialogBuilder(context)
            alertBuilder.setTitle("Confirmation")
            alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
            alertBuilder.setMessage(context.getString(R.string.trans_delete_warning_msg))
            alertBuilder.setCancelable(false)
            alertBuilder.setPositiveButton("Delete") { dialog, which ->
                isDeleted = myDatabaseHelper.deleteTransactions(transactions.transId)
                dialog.dismiss()
            }
            alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            val dialog = alertBuilder.create()
            dialog.show()
            dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
            if (isDeleted) {
                HelperFunctions.makeToast(context, context.getString(R.string.trans_deleted))
            }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val inputFormatter = DateTimeFormatterBuilder().parseCaseInsensitive()
                        .append(
                            DateTimeFormatter.ofPattern(
                                Constants.DATE_TIME_PATTERN,
                                Locale.getDefault()
                            )
                        )
                        .toFormatter()
                    val parsed = LocalDateTime.parse(transactions.date, inputFormatter)
                    val outputFormatter =
                        DateTimeFormatter.ofPattern(
                            Constants.DATE_SHORT_PATTERN,
                            Locale.getDefault()
                        )
                    tvDate.text = outputFormatter.format(parsed)
                } else {
                    val sdfOld = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
                    val date: Date = sdfOld.parse(transactions.date)!!
                    val sdfNew = SimpleDateFormat(Constants.DATE_SHORT_PATTERN, Locale.getDefault())
                    tvDate.text = sdfNew.format(date)
                }
            } catch (pe: Exception) {
                Log.e(Constants.LOG_TAG, pe.message, pe)
                FirebaseCrashlytics.getInstance().recordException(pe)
            }

            tvDesc.text = transactions.description
            if (transactions.debit == 0.toDouble()) {
                tvDeb.text = ""
            } else {
                tvDeb.text = HelperFunctions.getRoundedValue(transactions.debit)
            }
            if (transactions.credit == 0.toDouble()) {
                tvCre.text = ""
            } else {
                tvCre.text = HelperFunctions.getRoundedValue(transactions.credit)
            }
        }
    }
}
