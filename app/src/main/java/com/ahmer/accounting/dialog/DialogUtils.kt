package com.ahmer.accounting.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.Transactions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils

object DialogUtils {

    @JvmStatic
    fun deleteUser(context: Context, userId: Long, userName: String) {
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

    @JvmStatic
    fun deleteTransaction(context: Context, transId: Long) {
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

    @JvmStatic
    fun showDialogOptions(context: Context, trans: Transactions) {
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
                        val dialogTransEdit = TransactionsEdit(context, trans)
                        val dialogWindow: Window? = dialogTransEdit.window
                        dialogWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialogTransEdit.show()
                    }
                    1 -> {
                        val dialogTransInfo = TransactionsInfo(context, trans)
                        val dialogWindow: Window? = dialogTransInfo.window
                        dialogWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialogTransInfo.show()
                    }
                    2 -> {
                        deleteTransaction(context, trans.transId)
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
}