package com.example.dchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dchat.ui.Screen
import com.example.dchat.ui.auth.LoginScreen
import com.example.dchat.ui.auth.SignupScreen
import com.example.dchat.ui.chat.ChatScreen
import com.example.dchat.ui.home.HomeScreen
import com.example.dchat.ui.theme.DChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DChatTheme {
                DChatAppNavigation()
            }
        }
    }
}

@Composable
fun DChatAppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                users = emptyList(), // Will be populated by ViewModel later
                onUserClick = { user ->
                    navController.navigate(Screen.Chat.createRoute(user.id, user.username))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("receiverId") { type = NavType.StringType },
                navArgument("receiverName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: "Chat"
            ChatScreen(
                receiverName = receiverName,
                messages = emptyList(), // Will be populated by ViewModel later
                currentUserId = "temp_user_id", // Will be fetched from Auth later
                onSendMessage = { /* Will be handled by ViewModel later */ },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
