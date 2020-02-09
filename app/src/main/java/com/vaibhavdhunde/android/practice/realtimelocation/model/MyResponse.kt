@file:Suppress("SpellCheckingInspection")

package com.vaibhavdhunde.android.practice.realtimelocation.model

data class MyResponse(
    val multicast_id: Long = 0,
    val success: Int = 0,
    val failure:Int = 0,
    val canonical_ids: Int = 0,
    val results: List<Result>?= null
)