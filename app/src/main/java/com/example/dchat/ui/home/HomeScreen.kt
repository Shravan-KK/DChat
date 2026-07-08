package com.example.dchat.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dchat.data.model.User
import androidx.compose.ui.tooling.preview.Preview
import com.example.dchat.ui.theme.DChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    users: List<User>,
    onUserClick: (User) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DChat") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(users) { user ->
                UserItem(user = user, onClick = { onUserClick(user) })
                HorizontalDivider()
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
        HomeScreen(
            users = listOf(
                User("1", "Alby", "alby@example.com"),
                User("2", "Hari", "hari@example.com")
            ),
            onUserClick = {},
            onLogout = {}
        )
    }
}
