package com.example.moneytracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.moneytracker.R
import com.example.moneytracker.presentation.screens.add.AddCategoryScreen
import com.example.moneytracker.presentation.screens.add.AddTransactionScreen
import com.example.moneytracker.presentation.screens.category.CategoryScreen
import com.example.moneytracker.presentation.screens.home.HomeScreen
import com.example.moneytracker.presentation.screens.report.ReportScreenWithAi
import com.example.moneytracker.presentation.screens.transactions.AllTransactionsScreen

sealed class Screen(
    val route: String,
    val titleResId: Int
) {
    object Home : Screen("home", R.string.transactions)
    object Reports : Screen("reports", R.string.reports)
    object Categories : Screen("categories", R.string.categories)
    object AllTransactions : Screen("all_transactions", R.string.all_transactions)
    
    object AddTransaction : Screen("add_transaction", R.string.add_transaction) {
        const val ARG_TRANSACTION_ID = "transactionId"
        fun createRoute(transactionId: Int? = null) = 
            if (transactionId != null) "$route?$ARG_TRANSACTION_ID=$transactionId" else route
    }
    
    object AddCategory : Screen("add_category", R.string.add_category) {
        const val ARG_CATEGORY_ID = "categoryId"
        fun createRoute(categoryId: Int? = null) = 
            if (categoryId != null) "$route?$ARG_CATEGORY_ID=$categoryId" else route
    }
    
    companion object {
        val BottomNavItems = listOf(Home, Reports, Categories)
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        mainGraph(navController)
        addTransactionGraph(navController)
        addCategoryGraph(navController)
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        route = "main",
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddTransactionClick = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                },
                onTransactionClick = { transaction ->
                    navController.navigate(Screen.AddTransaction.createRoute(transaction.id))
                },
                onSeeAllTransactionsClick = {
                    navController.navigate(Screen.AllTransactions.route)
                }
            )
        }
        
        composable(Screen.Reports.route) {
            ReportScreenWithAi()
        }
        
        composable(Screen.Categories.route) {
            CategoryScreen(
                onAddCategoryClick = {
                    navController.navigate(Screen.AddCategory.createRoute())
                },
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.AddCategory.createRoute(categoryId))
                }
            )
        }
        
        composable(Screen.AllTransactions.route) {
            AllTransactionsScreen(
                onBackClick = { navController.navigateUp() },
                onTransactionClick = { transaction ->
                    navController.navigate(Screen.AddTransaction.createRoute(transaction.id))
                }
            )
        }
    }
}

private fun NavGraphBuilder.addTransactionGraph(navController: NavController) {
    composable(
        route = "${Screen.AddTransaction.route}?${Screen.AddTransaction.ARG_TRANSACTION_ID}={${Screen.AddTransaction.ARG_TRANSACTION_ID}}",
        arguments = emptyList()
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString(Screen.AddTransaction.ARG_TRANSACTION_ID)?.toIntOrNull()
        AddTransactionScreen(
            transactionId = transactionId,
            onBackClick = { navController.navigateUp() }
        )
    }
}

private fun NavGraphBuilder.addCategoryGraph(navController: NavController) {
    composable(
        route = "${Screen.AddCategory.route}?${Screen.AddCategory.ARG_CATEGORY_ID}={${Screen.AddCategory.ARG_CATEGORY_ID}}",
        arguments = emptyList()
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getString(Screen.AddCategory.ARG_CATEGORY_ID)?.toIntOrNull()
        AddCategoryScreen(
            categoryId = categoryId,
            onBackClick = { navController.navigateUp() }
        )
    }
}
