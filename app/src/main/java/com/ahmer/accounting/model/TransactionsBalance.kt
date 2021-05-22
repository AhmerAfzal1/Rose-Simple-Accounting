package com.ahmer.accounting.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ahmer.accounting.BR

class TransactionsBalance : BaseObservable() {

    @get:Bindable
    var accountType = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.accountType)
        }

    @get:Bindable
    var totalAmount = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalAmount)
        }

    @get:Bindable
    var cvBgColor = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cvBgColor)
        }

    @get:Bindable
    var tvColor = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tvColor)
        }

}