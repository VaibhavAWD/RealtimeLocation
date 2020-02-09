@file:Suppress("SpellCheckingInspection")

package com.vaibhavdhunde.android.practice.realtimelocation.remote

import com.vaibhavdhunde.android.practice.realtimelocation.model.MyResponse
import com.vaibhavdhunde.android.practice.realtimelocation.model.Request
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyApi {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=$AUTH_KEY"
    )
    @POST("fcm/send")
    suspend fun sendFriendRequestToUser(
        @Body body: Request
    ): Response<MyResponse>

    companion object {
        const val AUTH_KEY =
            "AAAAD0k64s4:APA91bF55uI-v3VnHqN-FPm6KRfu5AQUS4ti1SsYyCbyxaXEBhHUs7FgjU_ZAn7_cajqtjXUZ8DKV_Xy55G1ElfOD4C6g1eU0MR_nZamMX1OVrwsc_0X5mqyvdzPYtiqGnsekFrgXg7V"

        operator fun invoke(interceptor: NetworkInterceptor): MyApi {

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .client(client)
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}