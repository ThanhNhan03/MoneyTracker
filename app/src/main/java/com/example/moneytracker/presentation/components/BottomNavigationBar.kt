package com.example.moneytracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneytracker.R
import com.example.moneytracker.presentation.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Reports,
        Screen.Categories
    )
    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    val isSelected = currentRoute == screen.route
                    val icon = when (screen) {
                        Screen.Home -> if (isSelected) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
                        Screen.Reports -> if (isSelected) Icons.Filled.BarChart else Icons.Outlined.BarChart
                        Screen.Categories -> if (isSelected) Icons.Filled.Category else Icons.Outlined.Category
                        else -> Icons.Filled.AccountBalanceWallet
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = screen.titleResId)
                    )
                },
                label = { Text(stringResource(id = screen.titleResId)) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
