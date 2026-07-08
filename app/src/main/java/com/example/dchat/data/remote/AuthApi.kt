package com.example.dchat.data.remote

import com.example.dchat.data.model.AuthResponse
import com.example.dchat.data.model.LoginRequest
import com.example.dchat.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>
}
