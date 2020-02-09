package com.vaibhavdhunde.android.practice.realtimelocation.service

import android.content.Context
import android.os.Bundle
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.model.RequestData
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.util.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RequestTasks {

    const val ACTION_ACCEPT_REQUEST = "accept-request"
    const val ACTION_DECLINE_REQUEST = "decline-request"

    fun executeTask(context: Context, action: String, data: Bundle) {
        when (action) {
            ACTION_ACCEPT_REQUEST -> acceptRequest(context, data)
            ACTION_DECLINE_REQUEST -> declineRequest(context, data)
        }
    }

    private fun acceptRequest(context: Context, data: Bundle) {
        val uid = data.getString(RequestData.FROM_UID)!!
        val name = data.getString(RequestData.FROM_NAME)!!
        val email = data.getString(RequestData.FROM_EMAIL)!!
        val user = User(uid, name, email)

        FireDb.deleteRequest(uid)
        CoroutineScope(Dispatchers.IO).launch {
            FireDb.addToAcceptList(user)
        }
        NotificationUtils.clearAllNotifications(context)
    }

    private fun declineRequest(context: Context, data: Bundle) {
        val uid = data.getString(RequestData.FROM_UID)!!
        FireDb.deleteRequest(uid)
        NotificationUtils.clearAllNotifications(context)
    }
}