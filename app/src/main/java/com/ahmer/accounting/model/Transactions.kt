package com.ahmer.accounting.model

import android.app.DatePickerDialog
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ahmer.accounting.BR
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import java.text.SimpleDateFormat
import java.util.*

class Transactions : BaseObservable() {
    @get:Bindable
    var userId: Long = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.userId)
        }

    @get:Bindable
    var transId: Long = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.transId)
        }

    @get:Bindable
    var date: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @get:Bindable
    var description: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
        }

    @get:Bindable
    var credit: Double = 0.toDouble()
        set(value) {
            field = value
            notifyPropertyChanged(BR.credit)
        }

    @get:Bindable
    var debit: Double = 0.toDouble()
        set(value) {
            field = value
            notifyPropertyChanged(BR.debit)
        }

    var isDebit: Boolean = false

    @get:Bindable
    var created: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.created)
        }

    @get:Bindable
    var modified: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.modified)
        }

    @get:Bindable
    var modifiedValue: Double = 0.toDouble()
        set(value) {
            field = value
            notifyPropertyChanged(BR.modifiedValue)
        }

    @get:Bindable
    var modifiedAccountType: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.modifiedAccountType)
        }

    fun hasCreditZero(): Boolean {
        return credit == 0.toDouble()
    }

    fun hasDebitZero(): Boolean {
        return debit == 0.toDouble()
    }

    fun hasModifiedValueZero(): Boolean {
        return modifiedValue == 0.toDouble()
    }

    fun dateTimePickerShow(view: View) {
        val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            currentDate.set(year, month, dayOfMonth)
            date = dateFormat.format(currentDate.time)
        }
        val datePicker = DatePickerDialog(
            view.context,
            listener,
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setTitle("Select date")
        datePicker.show()
    }
}