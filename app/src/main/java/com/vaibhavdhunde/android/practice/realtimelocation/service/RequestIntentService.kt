package com.vaibhavdhunde.android.practice.realtimelocation.service

import android.app.IntentService
import android.content.Intent

class RequestIntentService : IntentService("RequestIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val action = it.action!!
            val data = it.extras!!
            RequestTasks.executeTask(this, action, data)
        }
    }
}