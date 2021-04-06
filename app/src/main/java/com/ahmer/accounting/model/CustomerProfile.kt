package com.ahmer.accounting.model

class CustomerProfile {

    var id: Int = 0

    var name: String = ""

    var gender: String = ""

    var address: String = ""

    var city: String = ""

    var phone1: String = ""

    var phone2: String = ""

    var phone3: String = ""

    var comment: String = ""

    override fun toString(): String {
        return "[$id] $name"
    }
}