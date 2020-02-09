package com.vaibhavdhunde.android.practice.realtimelocation.ui.explore

import com.vaibhavdhunde.android.practice.realtimelocation.model.MyResponse
import com.vaibhavdhunde.android.practice.realtimelocation.model.Request
import com.vaibhavdhunde.android.practice.realtimelocation.remote.MyApi
import com.vaibhavdhunde.android.practice.realtimelocation.remote.SafeApiRequest

class MyRemoteRepo(
    private val api: MyApi
) : RemoteRepo {

    override suspend fun sendFriendRequest(request: Request): MyResponse {
        return SafeApiRequest.apiRequest { api.sendFriendRequestToUser(request) }
    }
}