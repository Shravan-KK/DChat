package com.example.dchat.data.remote.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() {
    private var socket: Socket? = null
    private val TAG = "SocketManager"
    private var baseUrl: String = ""

    fun init(url: String) {
        if (socket != null && baseUrl == url) return
        baseUrl = url
        try {
            val opts = IO.Options().apply {
                forceNew = true
                reconnection = true
                // Localtunnel bypass headers
                val headers = mutableMapOf<String, List<String>>()
                headers["bypass-tunnel-reminder"] = listOf("true")
                extraHeaders = headers
            }
            socket = IO.socket(url, opts)
            
            socket?.on(Socket.EVENT_CONNECT) { Log.d(TAG, "Socket CONNECTED") }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args -> Log.e(TAG, "Socket CONNECTION ERROR: ${args.getOrNull(0)}") }
            socket?.on(Socket.EVENT_DISCONNECT) { Log.d(TAG, "Socket DISCONNECTED") }
            
            Log.d(TAG, "Socket initialized with URL: $url")
        } catch (e: Exception) {
            Log.e(TAG, "Socket initialization failed", e)
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun emitJoin(userId: String) {
        socket?.emit("join", userId)
    }

    fun emitSendMessage(senderId: String, receiverId: String, content: String) {
        val data = JSONObject().apply {
            put("senderId", senderId)
            put("receiverId", receiverId)
            put("content", content)
        }
        socket?.emit("send_message", data)
    }

    fun observeMessages(): Flow<JSONObject> = callbackFlow {
        val listener = { args: Array<Any> ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                trySend(data)
            }
        }
        socket?.on("receive_message", listener)
        awaitClose { socket?.off("receive_message", listener) }
    }

    fun observeConfirmations(): Flow<JSONObject> = callbackFlow {
        val listener = { args: Array<Any> ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                trySend(data)
            }
        }
        socket?.on("message_sent", listener)
        awaitClose { socket?.off("message_sent", listener) }
    }
}
