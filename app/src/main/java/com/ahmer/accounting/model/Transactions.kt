package com.ahmer.accounting.model

class Transactions {

    var userId: Long = 0

    var transId: Long = 0

    var date: String = ""

    var description: String = ""

    var credit: Double = 0.toDouble()

    var debit: Double = 0.toDouble()

    var isDebit: Boolean = false

    var created: String = ""

    var modified: String = ""

    var modifiedValue: Double = 0.toDouble()

    var modifiedAccountType: String = ""

}