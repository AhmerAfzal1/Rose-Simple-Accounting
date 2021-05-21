package com.ahmer.accounting.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ahmer.accounting.R
import com.ahmer.accounting.dialog.TransactionsInfo
import com.ahmer.accounting.model.Transactions
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

@Suppress("DEPRECATION")
class MyDialogs {

    companion object {

        fun showTransEditDialog(context: Context, trans: Transactions) {
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
                val toggleGroup =
                    dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
                val btnCredit = dialog.findViewById<MaterialButton>(R.id.toggleBtnCredit)
                val btnDebit = dialog.findViewById<MaterialButton>(R.id.toggleBtnDebit)
                val inputAmount = dialog.findViewById<TextInputLayout>(R.id.textInputLayoutAmount)
                val inputDescription =
                    dialog.findViewById<TextInputLayout>(R.id.textInputLayoutDescription)
                val addTransaction = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
                val cancelTransaction =
                    dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

                var newAccountType = ""
                toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
                    val checkedButton = dialog.findViewById<MaterialButton>(checkedId)
                    newAccountType = checkedButton.text.toString()
                    Log.v(Constants.LOG_TAG, "AccountType: $newAccountType")
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            when (newAccountType) {
                                context.getString(R.string.credit_plus) -> {
                                    btnCredit.backgroundTintList =
                                        ContextCompat.getColorStateList(context, R.color.black)
                                    btnDebit.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            context, android.R.color.transparent
                                        )
                                }
                                else -> {
                                    btnCredit.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            context, android.R.color.transparent
                                        )
                                    btnDebit.backgroundTintList =
                                        ContextCompat.getColorStateList(context, R.color.black)
                                }
                            }
                        }
                    }
                }

                val lastAmountType: String
                val lastAmountValue: Double
                val oldAccountType: String = if (trans.credit == 0.toDouble()) {
                    context.getString(R.string.debit_minus)
                } else {
                    context.getString(R.string.credit_plus)
                }
                if (oldAccountType == context.getString(R.string.debit_minus)) {
                    toggleGroup.check(R.id.toggleBtnDebit)
                    inputAmount.editText?.setText(trans.debit.toString())
                    lastAmountType = "Debit"
                    lastAmountValue = trans.debit
                } else {
                    toggleGroup.check(R.id.toggleBtnCredit)
                    inputAmount.editText?.setText(trans.credit.toString())
                    lastAmountType = "Credit"
                    lastAmountValue = trans.credit
                }
                inputDate.editText?.setText(HelperFunctions.getDateTime())
                inputDescription.editText?.setText(trans.description)
                addTransaction.text = context.getString(R.string.update)
                addTransaction.setOnClickListener {
                    val myDatabaseHelper = MyDatabaseHelper()
                    val newAmount: Double = inputAmount.editText?.text.toString().trim().toDouble()
                    when {
                        newAmount == 0.toDouble() -> {
                            ToastUtils.showLong(context.getString(R.string.enter_the_amount))
                        }
                        oldAccountType.isEmpty() -> {
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
                                if (newAccountType == context.getString(R.string.credit_plus)) {
                                    credit = newAmount
                                    isDebit = false
                                }
                                if (newAccountType == context.getString(R.string.debit_minus)) {
                                    debit = newAmount
                                    isDebit = true
                                }
                                date = inputDate.editText?.text.toString()
                                description = inputDescription.editText?.text.toString()
                                modified = HelperFunctions.getDateTime()
                                modifiedAccountType = lastAmountType
                                modifiedValue = lastAmountValue
                            }
                            isSuccessfullyUpdated =
                                myDatabaseHelper.updateTransactions(
                                    trans.transId,
                                    transContentValues
                                )
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

        fun showDropDownDialog(context: Context, trans: Transactions) {
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
                           val dialog1 = TransactionsInfo(context, trans)
                            val dialogWindow: Window? = dialog1.window
                            dialogWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog1.show()
                            dialogWindow.setLayout(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        }
                        2 -> {
                            confirmTransDelete(context, trans.transId)
                        }
                    }
                    dialog.dismiss()
                }
                val dialog = dialogBuilder.create()
                dialog.show()
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.getColor(R.color.black))
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        fun confirmUserDelete(context: Context, userId: Long, userName: String) {
            try {
                val myDatabaseHelper = MyDatabaseHelper()
                val alertBuilder = MaterialAlertDialogBuilder(context)
                alertBuilder.setTitle(context.getString(R.string.confirmation))
                alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
                alertBuilder.setMessage(
                    context.getString(R.string.user_delete_warning_msg, userName)
                )
                alertBuilder.setCancelable(false)
                alertBuilder.setPositiveButton(context.getString(R.string.delete)) { dialog, which ->
                    val isDeleted: Boolean = myDatabaseHelper.deleteUserProfileData(userId)
                    if (isDeleted) {
                        ToastUtils.showShort(context.getString(R.string.trans_deleted))
                    }
                    dialog.dismiss()
                }
                alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = alertBuilder.create()
                dialog.show()
                dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.getColor(R.color.black))
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.resources.getColor(R.color.black))
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        fun confirmTransDelete(context: Context, transId: Long) {
            try {
                val myDatabaseHelper = MyDatabaseHelper()
                val alertBuilder = MaterialAlertDialogBuilder(context)
                alertBuilder.setTitle(context.getString(R.string.confirmation))
                alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
                alertBuilder.setMessage(context.getString(R.string.trans_delete_warning_msg))
                alertBuilder.setCancelable(false)
                alertBuilder.setPositiveButton(context.getString(R.string.delete)) { dialog, which ->
                    val isDeleted: Boolean = myDatabaseHelper.deleteTransactions(transId)
                    if (isDeleted) {
                        ToastUtils.showShort(context.getString(R.string.trans_deleted))
                    }
                    dialog.dismiss()
                }
                alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = alertBuilder.create()
                dialog.show()
                dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.getColor(R.color.black))
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.resources.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(context.resources.getColor(R.color.black))
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}