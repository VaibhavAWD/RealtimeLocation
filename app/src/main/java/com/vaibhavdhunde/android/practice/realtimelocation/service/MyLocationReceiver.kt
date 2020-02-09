@file:Suppress("SpellCheckingInspection")

package com.vaibhavdhunde.android.practice.realtimelocation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import com.vaibhavdhunde.android.practice.realtimelocation.data.PaperUtil
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import io.paperdb.Paper

class MyLocationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_UPDATE_LOCATION =
            "com.vaibhavdhunde.android.practice.realtimelocation.action.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val action = intent.action
            if (action == ACTION_UPDATE_LOCATION) {
                val result = LocationResult.extractResult(intent)
                result?.let {
                    val location = result.lastLocation
                    Paper.init(context)
                    val uid = PaperUtil.getUserUid()!!
                    FireDb.updateLocation(uid, location)
                }
            }
        }
    }
}