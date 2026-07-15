package com.example.dchat.repository

import com.example.dchat.data.local.TokenManager
import com.example.dchat.data.model.Message
import com.example.dchat.data.remote.MessageApi
import com.example.dchat.data.remote.socket.SocketManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val messageApi: MessageApi,
    private val socketManager: SocketManager,
    private val tokenManager: TokenManager
) {
    suspend fun getChatHistory(receiverId: String): List<Message> {
        val token = tokenManager.getToken().firstOrNull()
        return try {
            val response = messageApi.getChatHistory("Bearer $token", receiverId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun markAsRead(senderId: String) {
        val token = tokenManager.getToken().firstOrNull()
        try {
            messageApi.markAsRead("Bearer $token", senderId)
        } catch (e: Exception) {
            // Silently fail for now
        }
    }

    fun initSocket(url: String) = socketManager.init(url)
    fun joinRoom(userId: String) = socketManager.emitJoin(userId)
    fun sendMessage(senderId: String, receiverId: String, content: String) = 
        socketManager.emitSendMessage(senderId, receiverId, content)

    fun getIncomingMessages(): Flow<Message> = socketManager.observeMessages().map { jsonToMessage(it) }
    fun getSentConfirmations(): Flow<Message> = socketManager.observeConfirmations().map { jsonToMessage(it) }

    private fun jsonToMessage(json: JSONObject): Message {
        return Message(
            id = if (json.isNull("id")) null else json.getLong("id"),
            senderId = json.getString("sender_id"),
            receiverId = json.getString("receiver_id"),
            content = json.getString("content"),
            timestamp = json.optString("timestamp"),
            isRead = json.optBoolean("is_read", false)
        )
    }
}
