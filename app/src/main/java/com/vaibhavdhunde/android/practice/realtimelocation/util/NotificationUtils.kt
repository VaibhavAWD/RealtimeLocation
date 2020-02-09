@file:Suppress("SpellCheckingInspection", "DEPRECATION")

package com.vaibhavdhunde.android.practice.realtimelocation.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.model.RequestData
import com.vaibhavdhunde.android.practice.realtimelocation.service.RequestIntentService
import com.vaibhavdhunde.android.practice.realtimelocation.service.RequestTasks
import com.vaibhavdhunde.android.practice.realtimelocation.ui.requests.RequestsActivity


object NotificationUtils {

    private const val NOTIFICATION_CHANNEL_ID =
        "com.vaibhavdhunde.android.practice.realtimelocation"
    private const val NOTIFICATION_CHANNEL_NAME = "Realtime Location"
    private const val NOTIFICATION_ID = 2768
    private const val PENDING_INTENT_ID = 7873
    private const val ACTION_ACCEPT_REQUEST_INTENT_ID = 6326
    private const val ACTION_DECLINE_REQUEST_INTENT_ID = 1904

    fun sendNotification(context: Context, data: Map<String, String>) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(context, data))
    }

    fun clearAllNotifications(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun buildNotification(context: Context, data: Map<String, String>): Notification {

        val title = context.getString(R.string.notification_title_friend_request)
        val content = "New friend request from ${data[RequestData.FROM_NAME]} " +
                "(${data[RequestData.FROM_EMAIL]})"

        val builder = NotificationCompat.Builder(
            context, NOTIFICATION_CHANNEL_ID
        )
        builder.color = ContextCompat.getColor(context, R.color.colorPrimary)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(content))
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setContentIntent(getContentIntent(context))
        builder.addAction(createAcceptRequestAction(context, data))
        builder.addAction(createDeclineRequestAction(context, data))
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        ) {
            builder.priority = NotificationCompat.PRIORITY_HIGH
        }

        return builder.build()
    }

    private fun createAcceptRequestAction(
        context: Context,
        data: Map<String, String>
    ): NotificationCompat.Action {
        val bundle = Bundle()
        bundle.putString(RequestData.FROM_UID, data.getValue(RequestData.FROM_UID))
        bundle.putString(RequestData.FROM_NAME, data.getValue(RequestData.FROM_NAME))
        bundle.putString(RequestData.FROM_EMAIL, data.getValue(RequestData.FROM_EMAIL))

        val acceptRequestPendingIntent = Intent(context, RequestIntentService::class.java)
        acceptRequestPendingIntent.action = RequestTasks.ACTION_ACCEPT_REQUEST
        acceptRequestPendingIntent.putExtras(bundle)
        val incrementWaterCountPendingIntent = PendingIntent.getService(
            context,
            ACTION_ACCEPT_REQUEST_INTENT_ID,
            acceptRequestPendingIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Action(
            R.drawable.ic_done,
            context.getString(R.string.btn_accept),
            incrementWaterCountPendingIntent
        )
    }

    private fun createDeclineRequestAction(
        context: Context,
        data: Map<String, String>
    ): NotificationCompat.Action {
        val bundle = Bundle()
        bundle.putString(RequestData.FROM_UID, data.getValue(RequestData.FROM_UID))
        bundle.putString(RequestData.FROM_NAME, data.getValue(RequestData.FROM_NAME))
        bundle.putString(RequestData.FROM_EMAIL, data.getValue(RequestData.FROM_EMAIL))

        val declineRequestPendingIntent = Intent(context, RequestIntentService::class.java)
        declineRequestPendingIntent.action = RequestTasks.ACTION_DECLINE_REQUEST
        declineRequestPendingIntent.putExtras(bundle)
        val ignoreReminderPendingIntent = PendingIntent.getService(
            context,
            ACTION_DECLINE_REQUEST_INTENT_ID,
            declineRequestPendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action(
            R.drawable.ic_close,
            context.getString(R.string.btn_decline),
            ignoreReminderPendingIntent
        )
    }

    private fun getContentIntent(context: Context): PendingIntent {
        val requestsIntent = Intent(context, RequestsActivity::class.java)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntentWithParentStack(requestsIntent)
        return taskStackBuilder.getPendingIntent(
            PENDING_INTENT_ID, PendingIntent.FLAG_UPDATE_CURRENT
        )!!
    }

    @Suppress("unused")
    private fun getLargeIcon(context: Context): Bitmap {
        val res = context.resources
        return BitmapFactory.decodeResource(res, R.drawable.ic_notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}