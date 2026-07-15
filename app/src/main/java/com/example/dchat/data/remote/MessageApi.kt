package com.example.dchat.data.remote

import com.example.dchat.data.model.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {
    @GET("messages/{receiverId}")
    suspend fun getChatHistory(
        @Header("Authorization") token: String,
        @Path("receiverId") receiverId: String
    ): Response<List<Message>>

    @POST("messages/read/{senderId}")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("senderId") senderId: String
    ): Response<Unit>
}
