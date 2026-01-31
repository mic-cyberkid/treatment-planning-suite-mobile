package com.mosec.tpsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mosec.tpsuite.ui.theme.TPSuiteTheme

data class DashboardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String = "home"
)

val dashboardItems = listOf(
    DashboardItem("SAD Open Field", "Isocentric calculations", Icons.Default.GridView, "sadOpen"),
    DashboardItem("SAD Blocked", "Custom shielding factors", Icons.Default.Shield, "sadBlocked"),
    DashboardItem("SAD Wedged", "Physical & Virtual wedges", Icons.Default.ChangeHistory, "sadWedge"),
    DashboardItem("SSD Open", "Standard fixed distance", Icons.Default.Straighten, "ssdOpen"),
    DashboardItem("SSD Blocked", "Blocked field dosimetry", Icons.Default.Texture, "ssdBlocked"),
    DashboardItem("LINAC QA", "Daily & Monthly checks", Icons.AutoMirrored.Filled.FactCheck, "qa")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    onLogoutClick: () -> Unit,
    onFabClick: () -> Unit,
    onNavItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treatment Planning Suite") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle profile click */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
         /*floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },*/
        bottomBar = {
            BottomNavigationBar(onNavItemClick = onNavItemClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Physicist Suite",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Select a module to begin treatment planning.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardCard(item = item, onClick = { onNavItemClick(item.route) })
                }
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BottomNavigationBar(onNavItemClick: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { onNavItemClick("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "QA") },
            label = { Text("QA") },
            selected = false,
            onClick = { onNavItemClick("qa") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            selected = false,
            onClick = { onNavItemClick("history") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
            label = { Text("Admin") },
            selected = false,
            onClick = { onNavItemClick("adminDashboard") }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun UserDashboardScreenPreview() {
    TPSuiteTheme {
        UserDashboardScreen(
            onLogoutClick = {},
            onFabClick = {},
            onNavItemClick = {}
        )
    }
}
