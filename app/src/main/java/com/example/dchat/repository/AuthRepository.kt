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
            val body = response.body()
            if (body != null) {
                tokenManager.saveAuthData(body.token, body.user.id)
            }
        }
        return response
    }

    suspend fun signup(request: SignupRequest): Response<AuthResponse> {
        val response = authApi.signup(request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                tokenManager.saveAuthData(body.token, body.user.id)
            }
        }
        return response
    }

    fun getToken() = tokenManager.getToken()
    fun getUserId() = tokenManager.getUserId()

    suspend fun logout() {
        tokenManager.clearAll()
    }
}
