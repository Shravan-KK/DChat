package com.example.dchat.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("is_read") val isRead: Boolean = false
)
