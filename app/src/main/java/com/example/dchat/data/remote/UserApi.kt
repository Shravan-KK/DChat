package com.example.dchat.data.remote

import com.example.dchat.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<User>>
}
