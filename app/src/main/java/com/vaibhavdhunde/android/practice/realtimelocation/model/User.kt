package com.vaibhavdhunde.android.practice.realtimelocation.model

data class User(
    val uid: String,
    var name: String,
    val email: String,
    val tokens: String? = null,
    val acceptList: HashMap<String, User>? = null
) {
    constructor() : this("", "", "")
}

object UserField {
    const val UID = "uid"
    const val NAME = "name"
    const val EMAIL = "email"
    const val TOKENS = "tokens"
    const val ACCEPT_LIST = "acceptList"
}