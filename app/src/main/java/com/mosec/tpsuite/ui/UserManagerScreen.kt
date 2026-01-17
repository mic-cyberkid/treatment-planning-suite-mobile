package com.mosec.tpsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mosec.tpsuite.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagerScreen(
    viewModel: UserManagerViewModel,
    onNavigateBack: () -> Unit
) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                viewModel.resetForm()
                viewModel.showEditDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (users.isEmpty()) {
                Text(
                    "No users found. Add one to get started.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users) { user ->
                        UserCard(
                            user = user,
                            onEdit = { viewModel.onEditUser(user) },
                            onDelete = { viewModel.onDeleteUser(user) }
                        )
                    }
                }
            }
        }

        if (viewModel.showEditDialog) {
            UserEditDialog(viewModel)
        }
    }
}

@Composable
fun UserCard(user: User, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("@${user.username} • ${user.role}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditDialog(viewModel: UserManagerViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showEditDialog = false },
        title = { Text(if (viewModel.isEditMode) "Edit User" else "Add New User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.fullName,
                    onValueChange = { viewModel.fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!viewModel.isEditMode) {
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("Initial Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text("Role", style = MaterialTheme.typography.labelMedium)
                val roles = listOf("Physicist", "Dosimetrist", "Administrator")
                roles.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = viewModel.role == role,
                            onClick = { viewModel.role = role }
                        )
                        Text(role)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.saveUser() }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showEditDialog = false }) {
                Text("Cancel")
            }
        }
    )
}
