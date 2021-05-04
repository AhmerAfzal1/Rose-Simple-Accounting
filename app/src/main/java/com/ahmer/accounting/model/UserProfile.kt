package com.ahmer.accounting.model

class UserProfile {

    var id: Long = 0

    var name: String = ""

    var gender: String = ""

    var address: String = ""

    var city: String = ""

    var phone1: String = ""

    var phone2: String = ""

    var email: String = ""

    var comment: String = ""

    var created: String = ""

    var modified: String = ""

    override fun toString(): String {
        return "[$id] $name"
    }
}