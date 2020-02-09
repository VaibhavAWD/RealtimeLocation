package com.vaibhavdhunde.android.practice.realtimelocation.model

data class Request(
    var to: String,
    var data: Map<String, String>? = null
) {
    constructor() : this("")
}

object RequestData {
    const val FROM_UID = "fromUid"
    const val FROM_NAME = "fromName"
    const val FROM_EMAIL = "fromEmail"
    const val TO_UID = "toUid"
    const val TO_NAME = "toName"
    const val TO_EMAIL = "toEmail"
}