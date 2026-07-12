package com.example.dchat.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dchat.data.model.User
import com.example.dchat.repository.AuthRepository
import com.example.dchat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow<UsersState>(UsersState.Loading)
    val usersState: StateFlow<UsersState> = _usersState

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _usersState.value = UsersState.Loading
            try {
                val response = userRepository.getUsers()
                if (response.isSuccessful) {
                    _usersState.value = UsersState.Success(response.body() ?: emptyList())
                } else {
                    _usersState.value = UsersState.Error("Failed to fetch users")
                }
            } catch (e: Exception) {
                _usersState.value = UsersState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}

sealed class UsersState {
    object Loading : UsersState()
    data class Success(val users: List<User>) : UsersState()
    data class Error(val message: String) : UsersState()
}
