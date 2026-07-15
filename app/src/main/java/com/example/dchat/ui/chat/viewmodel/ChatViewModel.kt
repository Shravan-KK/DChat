package com.example.dchat.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dchat.data.local.TokenManager
import com.example.dchat.data.model.Message
import com.example.dchat.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val receiverId: String = checkNotNull(savedStateHandle["receiverId"])
    private var currentUserId: String = ""

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentUserIdState = MutableStateFlow("")
    val currentUserIdState: StateFlow<String> = _currentUserIdState

    init {
        setupChat()
    }

    private fun setupChat() {
        viewModelScope.launch {
            try {
                // 1. Get Identity
                val userId = tokenManager.getUserId().filterNotNull().first()
                currentUserId = userId
                _currentUserIdState.value = userId
                Log.d("ChatViewModel", "Identity: $userId chatting with $receiverId")

                // 2. Join Room
                repository.joinRoom(userId)

                // 3. Load History
                val history = repository.getChatHistory(receiverId)
                _messages.value = history
                Log.d("ChatViewModel", "History loaded: ${history.size}")

                // 4. Start Live Observers
                observeSocket()
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Setup failed", e)
            }
        }
    }

    private fun observeSocket() {
        // Observe Incoming
        repository.getIncomingMessages()
            .onEach { msg ->
                if (msg.senderId == receiverId) {
                    _messages.value = _messages.value + msg
                    // Mark as read immediately since we are viewing the chat
                    viewModelScope.launch {
                        repository.markAsRead(receiverId)
                    }
                }
            }
            .launchIn(viewModelScope)

        // Observe Confirmations
        repository.getSentConfirmations()
            .onEach { msg ->
                // Replace optimistic or just add
                _messages.value = _messages.value.filter { it.timestamp != "Sending..." } + msg
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(content: String) {
        if (content.isNotBlank() && currentUserId.isNotBlank()) {
            val optimistic = Message(
                senderId = currentUserId,
                receiverId = receiverId,
                content = content,
                timestamp = "Sending..."
            )
            _messages.value = _messages.value + optimistic
            repository.sendMessage(currentUserId, receiverId, content)
        }
    }
}
