package com.example.dchat.repository

import com.example.dchat.data.local.TokenManager
import com.example.dchat.data.model.AuthResponse
import com.example.dchat.data.model.LoginRequest
import com.example.dchat.data.model.SignupRequest
import com.example.dchat.data.remote.AuthApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        val response = authApi.login(request)
        if (response.isSuccessful) {
            response.body()?.token?.let { tokenManager.saveToken(it) }
        }
        return response
    }

    suspend fun signup(request: SignupRequest): Response<AuthResponse> {
        val response = authApi.signup(request)
        if (response.isSuccessful) {
            response.body()?.token?.let { tokenManager.saveToken(it) }
        }
        return response
    }

    fun getToken() = tokenManager.getToken()

    suspend fun logout() {
        tokenManager.clearToken()
    }
}
