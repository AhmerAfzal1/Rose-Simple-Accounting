package com.ahmer.accounting.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ahmer.accounting.R
import com.ahmer.accounting.model.Transactions
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

@Suppress("DEPRECATION")
class MyDialogs {

    companion object {

        fun showUserProfileInfo(context: Context, userProfile: UserProfile) {
            try {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.user_profile_data_dialog)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                val getID = dialog.findViewById<TextView>(R.id.dialogUserID)
                val getName = dialog.findViewById<TextView>(R.id.dialogUserName)
                val getGender = dialog.findViewById<TextView>(R.id.dialogUserGender)
                val getAddress = dialog.findViewById<TextView>(R.id.dialogUserAddress)
                val getCity = dialog.findViewById<TextView>(R.id.dialogUserCity)
                val getPhone1 = dialog.findViewById<TextView>(R.id.dialogUserPhone1)
                val getPhone2 = dialog.findViewById<TextView>(R.id.dialogUserPhone2)
                val getEmail = dialog.findViewById<TextView>(R.id.dialogUserEmail)
                val getComments = dialog.findViewById<TextView>(R.id.dialogUserComments)
                val getCreated = dialog.findViewById<TextView>(R.id.dialogUserCreated)
                val getModified = dialog.findViewById<TextView>(R.id.dialogUserModified)
                val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                /*
                Log.v(Constants.LOG_TAG, "Dialog ID: ${userProfile.id}")
                Log.v(Constants.LOG_TAG, "Dialog Name: ${userProfile.name}")
                Log.v(Constants.LOG_TAG, "Dialog Gender: ${userProfile.gender}")
                Log.v(Constants.LOG_TAG, "Dialog Address: ${userProfile.address}")
                Log.v(Constants.LOG_TAG, "Dialog Phone1: ${userProfile.phone1}")
                Log.v(Constants.LOG_TAG, "Dialog Phone2: ${userProfile.phone2}")
                Log.v(Constants.LOG_TAG, "Dialog Email: ${userProfile.email}")
                Log.v(Constants.LOG_TAG, "Dialog Comments: ${userProfile.comment}")
                Log.v(Constants.LOG_TAG, "Dialog Created: ${userProfile.created}")
                Log.v(Constants.LOG_TAG, "Dialog Modified: ${userProfile.modified}")
                */
                getID.text = userProfile.id.toString()
                getName.text = userProfile.name
                getGender.text = userProfile.gender
                getAddress.text = userProfile.address
                getCity.text = userProfile.city
                getPhone1.text = userProfile.phone1
                getPhone2.text = userProfile.phone2
                getEmail.text = userProfile.email
                getComments.text = userProfile.comment
                getCreated.text = userProfile.created
                getModified.text = userProfile.modified
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnOk.backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.black)
                        btnOk.setTextColor(context.getColor(R.color.white))
                    } else {
                        btnOk.setBackgroundColor(context.resources.getColor(R.color.black))
                        btnOk.setTextColor(context.resources.getColor(R.color.white))
                    }
                }
                btnOk.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        fun showTransInfoDialog(context: Context, trans: Transactions) {
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
                val toggle =
                    dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
                val inputAmount = dialog.findViewById<TextInputLayout>(R.id.textInputLayoutAmount)
                val inputDescription =
                    dialog.findViewById<TextInputLayout>(R.id.textInputLayoutDescription)
                val addTransaction = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
                val cancelTransaction =
                    dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

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
                addTransaction.text = context.getString(R.string.update)
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
                            showTransInfoDialog(context, trans)
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
                val myDatabaseHelper = MyDatabaseHelper(context)
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
                val myDatabaseHelper = MyDatabaseHelper(context)
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