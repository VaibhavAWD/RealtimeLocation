package com.vaibhavdhunde.android.practice.realtimelocation.model

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class MyLocation(
    val accuracy: Int = 0,
    val altitude: Int = 0,
    val bearing: Int = 0,
    val bearingAccuracyDegrees: Int = 0,
    val speed: Int = 0,
    val speedAccuracyMetersPerSecond: Int = 0,
    val verticalAccuracyMeters: Int = 0,
    val isComplete: Boolean = false,
    val isFromMockProvider: Boolean = false,
    val provider: String? = null,
    val time: Long = 0,
    val elapsedRealtimeNanos: Long = 0,
    val latitude: Double = 0.toDouble(),
    val longitude: Double = 0.toDouble()
) {
    val timeStamp
    get() = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
        .format(Date(Timestamp(time).time)).toString()
}