package com.example.moneytracker.presentation.screens.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.presentation.components.CategoryItem
import com.example.moneytracker.presentation.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Int) -> Unit = {}
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val showDeleteConfirmation: Category? by viewModel.showDeleteConfirmation.collectAsState()
    var selectedType by remember { mutableStateOf("expense") }

    LaunchedEffect(selectedType) {
        viewModel.loadCategories(selectedType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.categories)) },
                actions = {
                    IconButton(onClick = onAddCategoryClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_category)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "expense" to stringResource(R.string.expense),
                    "income" to stringResource(R.string.income)
                ).forEach { (type, label) ->
                    FilterChip(
                        selected = type == selectedType,
                        onClick = { selectedType = type },
                        label = { Text(label) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (error != null) {
                    Text(
                        text = "${stringResource(R.string.error)}: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (categories.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_categories),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                onCategoryClick = { onCategoryClick(category.id) },
                                onEditClick = { onCategoryClick(category.id) },
                                onDeleteClick = { viewModel.showDeleteConfirmation(category) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteConfirmation?.let { category: Category ->
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.delete_category_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteCategory(category) }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteConfirmation() }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
