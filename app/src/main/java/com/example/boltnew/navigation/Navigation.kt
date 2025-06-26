package com.example.boltnew.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.boltnew.presentation.viewmodel.AuthViewModel
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import com.example.boltnew.ui.screens.adverts.AdvertDetailScreen
import com.example.boltnew.ui.screens.adverts.AdvertScreen
import com.example.boltnew.ui.screens.ProfileScreen
import com.example.boltnew.ui.screens.auth.LoginScreen
import com.example.boltnew.ui.screens.auth.RegisterScreen
import com.example.boltnew.utils.RequestState
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)

    // Determine start destination based on auth state
    val startDestination = if (isLoggedIn) "advert" else "login"

    Scaffold(
        bottomBar = {
            // Only show bottom navigation for authenticated main screens
            if (isLoggedIn) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Authentication Screens
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("advert") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("advert") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // Main App Screens (Authenticated)
            composable("advert") {
                AdvertScreen(
                    onAdvertClick = { advertId ->
                        navController.navigate("advert_detail/$advertId")
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("advert_detail/{advertId}") { backStackEntry ->
                val advertId = backStackEntry.arguments?.getString("advertId")?.toIntOrNull() ?: 0
                AdvertDetailScreen(
                    advertId = advertId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Check if user has a profile
    val hasProfile = when (val userState = currentUser) {
        is RequestState.Success -> {
            val user = userState.data
            user.profile.documentId.isNotBlank()
        }
        else -> false
    }

    // Only show bottom navigation on main screens
    val showBottomNav = currentDestination?.route in listOf("advert", "profile")

    if (showBottomNav) {
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Adverts") },
                label = { Text("Adverts") },
                selected = currentDestination?.hierarchy?.any { it.route == "advert" } == true,
                onClick = {
                    navController.navigate("advert") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
            
            // Profile tab - show different behavior based on profile existence
            if (hasProfile) {
                // User has profile - show normal profile tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                // User doesn't have profile - show create profile button
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create Profile") },
                    label = { Text("Create Profile") },
                    selected = false,
                    onClick = {
                        // Show profile creation modal
                        profileViewModel.showProfileEditModal()
                    }
                )
            }
        }
    }
}