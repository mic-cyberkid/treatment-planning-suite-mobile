package com.mosec.tpsuite.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mosec.tpsuite.data.AppDatabase
import com.mosec.tpsuite.ui.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = { navController.navigate("login") })
        }
        composable("login") {
            LoginScreen(onLoginClick = { _, _ -> navController.navigate("dashboard") })
        }
        composable("dashboard") {
            UserDashboardScreen(
                onLogoutClick = { navController.navigate("login") },
                onFabClick = { navController.navigate("addUser") },
                onNavItemClick = { route -> navController.navigate(route) }
            )
        }

        composable("qa") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: LinacQaViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LinacQaViewModel(dao) as T
                }
            })
            LinacQAScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("sadOpen") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: SadOpenFieldViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SadOpenFieldViewModel(dao) as T
                }
            })
            SadOpenFieldScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("sadBlocked") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: SadBlockedFieldViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SadBlockedFieldViewModel(dao) as T
                }
            })
            SadBlockedFieldScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("sadWedge") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: SadWedgedFieldViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SadWedgedFieldViewModel(dao) as T
                }
            })
            SadWedgedFieldScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("ssdOpen") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: SsdOpenFieldViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SsdOpenFieldViewModel(dao) as T
                }
            })
            SsdOpenFieldScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("ssdBlocked") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: SsdBlockedFieldViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SsdBlockedFieldViewModel(dao) as T
                }
            })
            SsdBlockedFieldScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("adminDashboard") {
            AdminDashboardScreen(
                onNavigateToUserManager = { navController.navigate("userManager") },
                onNavigateToLogViewer = { navController.navigate("logViewer") },
                onLogout = { navController.navigate("login") { popUpTo("home") { inclusive = true } } }
            )
        }
        composable("userManager") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.userDao()
            val viewModel: UserManagerViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserManagerViewModel(dao) as T
                }
            })
            UserManagerScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("logViewer") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dao = database.dataLogDao()
            val viewModel: AdminLogViewerViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminLogViewerViewModel(dao) as T
                }
            })
            AdminLogViewerScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("history") {
            CalculationHistoryScreen()
        }
    }
}
