package com.example.dchat.repository

import com.example.dchat.data.local.TokenManager
import com.example.dchat.data.model.User
import com.example.dchat.data.remote.UserApi
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) {
    suspend fun getUsers(): Response<List<User>> {
        val token = tokenManager.getToken().firstOrNull()
        return userApi.getUsers("Bearer $token")
    }
}
