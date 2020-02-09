package com.vaibhavdhunde.android.practice.realtimelocation.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.util.NotificationUtils

class MyFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        if (FireAuth.isUserLoggedIn()) {
            FireDb.updateNewToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationUtils.sendNotification(this, message.data)
        FireDb.addRequest(message.data)
    }
}