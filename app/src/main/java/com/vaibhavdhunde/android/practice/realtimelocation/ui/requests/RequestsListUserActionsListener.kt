package com.vaibhavdhunde.android.practice.realtimelocation.ui.requests

import com.vaibhavdhunde.android.practice.realtimelocation.model.User

interface RequestsListUserActionsListener {

    fun onAcceptFriendRequest(user: User)

    fun onDeclineFriendRequest(user: User)
}