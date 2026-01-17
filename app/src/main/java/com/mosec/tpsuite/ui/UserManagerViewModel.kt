package com.mosec.tpsuite.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosec.tpsuite.data.User
import com.mosec.tpsuite.data.UserDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing users in the administrative section.
 */
class UserManagerViewModel(private val userDao: UserDao) : ViewModel() {

    val users: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var isEditMode by mutableStateOf(false)
    var selectedUser by mutableStateOf<User?>(null)

    // Form fields
    var username by mutableStateOf("")
    var fullName by mutableStateOf("")
    var password by mutableStateOf("")
    var role by mutableStateOf("Physicist")

    var showEditDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onEditUser(user: User) {
        selectedUser = user
        username = user.username
        fullName = user.fullName
        role = user.role
        password = "" // Don't show password or allow editing it here for safety
        isEditMode = true
        showEditDialog = true
    }

    fun onDeleteUser(user: User) {
        viewModelScope.launch {
            userDao.deleteUser(user)
        }
    }

    fun saveUser() {
        if (username.isBlank() || fullName.isBlank()) {
            errorMessage = "Username and Full Name are required"
            return
        }

        viewModelScope.launch {
            val user = if (isEditMode && selectedUser != null) {
                selectedUser!!.copy(
                    username = username,
                    fullName = fullName,
                    role = role
                    // Password preservation logic would go here in a full app
                )
            } else {
                User(
                    username = username,
                    fullName = fullName,
                    password = password,
                    role = role
                )
            }

            if (isEditMode) {
                userDao.updateUser(user)
            } else {
                userDao.insertUser(user)
            }
            
            showEditDialog = false
            resetForm()
        }
    }

    fun resetForm() {
        username = ""; fullName = ""; password = ""; role = "Physicist"
        isEditMode = false; selectedUser = null
    }
}
