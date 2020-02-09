package com.vaibhavdhunde.android.practice.realtimelocation.ui.explore

import com.vaibhavdhunde.android.practice.realtimelocation.model.MyResponse
import com.vaibhavdhunde.android.practice.realtimelocation.model.Request

interface RemoteRepo {

    suspend fun sendFriendRequest(request: Request): MyResponse
}