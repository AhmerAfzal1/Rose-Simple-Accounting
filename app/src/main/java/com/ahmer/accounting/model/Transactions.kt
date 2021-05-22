package com.ahmer.accounting.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ahmer.accounting.BR

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
    var credit: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.credit)
        }

    @get:Bindable
    var debit: String = ""
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
    var modifiedValue: String = ""
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

    @get:Bindable
    var enteredAmount: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.enteredAmount)
        }

    fun hasCreditZero(): Boolean {
        return credit == "0"
    }

    fun hasDebitZero(): Boolean {
        return debit == "0"
    }

    fun hasModifiedValueZero(): Boolean {
        return modifiedValue == "0"
    }
}