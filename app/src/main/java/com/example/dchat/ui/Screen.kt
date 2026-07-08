package com.example.dchat.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object Chat : Screen("chat/{receiverId}/{receiverName}") {
        fun createRoute(receiverId: String, receiverName: String) = "chat/$receiverId/$receiverName"
    }
}
