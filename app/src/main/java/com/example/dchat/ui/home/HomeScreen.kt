package com.example.dchat.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dchat.data.model.User
import androidx.compose.ui.tooling.preview.Preview
import com.example.dchat.ui.theme.DChatTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dchat.ui.home.viewmodel.HomeViewModel
import com.example.dchat.ui.home.viewmodel.UsersState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUserClick: (User) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DChat") },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (usersState) {
                is UsersState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UsersState.Error -> {
                    Text(
                        text = (usersState as UsersState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UsersState.Success -> {
                    val users = (usersState as UsersState.Success).users
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(users) { user ->
                            UserItem(user = user, onClick = { onUserClick(user) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(user.username) },
        supportingContent = { Text(user.email) },
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DChatTheme {
        // Updated preview logic could go here if needed, or keeping it simple
        UserItem(user = User("1", "Alby", "alby@example.com"), onClick = {})
    }
}
