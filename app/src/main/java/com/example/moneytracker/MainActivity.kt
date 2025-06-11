package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.presentation.components.BottomNavigationBar
import com.example.moneytracker.presentation.navigation.NavGraph
import com.example.moneytracker.presentation.navigation.Screen
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyTrackerAppContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackerAppContent() {
    MoneyTrackerTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        // Check if current destination is a bottom nav item
        val isBottomBarVisible = Screen.BottomNavItems.any { screen ->
            currentDestination?.route?.startsWith(screen.route) == true
        }
        
        // Scaffold with BottomNavigation
        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    BottomNavigationBar(navController = navController)
                }
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                NavGraph(navController = navController)
            }
        }
    }
}