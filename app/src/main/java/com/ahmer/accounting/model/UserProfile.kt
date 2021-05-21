package com.ahmer.accounting.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ahmer.accounting.BR

class UserProfile : BaseObservable() {

    @get:Bindable
    var id: Long = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var gender: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.gender)
        }

    @get:Bindable
    var address: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address)
        }

    @get:Bindable
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    @get:Bindable
    var phone1: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phone1)
        }

    @get:Bindable
    var phone2: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phone2)
        }

    @get:Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @get:Bindable
    var comment: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }

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

    override fun toString(): String {
        return "[$id] $name"
    }
}