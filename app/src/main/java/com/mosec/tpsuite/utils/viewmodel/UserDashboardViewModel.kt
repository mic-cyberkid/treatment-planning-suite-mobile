package com.mosec.tpsuite.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosec.tpsuite.ui.DashboardItem
import com.mosec.tpsuite.ui.dashboardItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserDashboardState(
    val items: List<DashboardItem> = emptyList()
)

class UserDashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(UserDashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboardItems()
    }

    private fun loadDashboardItems() {
        viewModelScope.launch {
            _state.value = UserDashboardState(items = dashboardItems)
        }
    }

    fun logout() {
        // TODO: Implement actual logout logic
    }
}
