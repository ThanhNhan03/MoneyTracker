package com.example.moneytracker.presentation.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.presentation.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onBackClick: () -> Unit,
    categoryId: Int? = null,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    // Load category data if in edit mode
    LaunchedEffect(categoryId) {
        categoryId?.let { viewModel.loadCategory(it) }
    }
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("expense") }
    var icon by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    // Observe the category to edit
    val categoryToEdit by viewModel.categoryToEdit.collectAsState()
    
    // Update UI when category data is loaded
    LaunchedEffect(categoryToEdit) {
        categoryToEdit?.let { category ->
            name = category.name
            selectedType = category.type
            icon = category.icon
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (categoryId != null) 
                            stringResource(R.string.edit_category)
                        else 
                            stringResource(R.string.add_category)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Type Selection
            Text(
                text = stringResource(R.string.category_type),
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "expense" to stringResource(R.string.expense),
                    "income" to stringResource(R.string.income)
                ).forEach { (type, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = (type == selectedType),
                                onClick = { selectedType = type },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (type == selectedType),
                            onClick = { selectedType = type }
                        )
                        Text(label)
                    }
                }
            }

            // Category Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    showError = false
                },
                label = { Text(stringResource(R.string.category_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text(stringResource(R.string.error_field_required))
                    }
                }
            )

            // Icon Selection (Placeholder)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = icon,
                    onValueChange = { /* Read-only, so no-op */ },
                    label = { Text(stringResource(R.string.category_icon)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )
                // Invisible clickable overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            // TODO: Show icon picker
                        }
                )
            }

            // Save or Update Button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        if (categoryId != null) {
                            viewModel.updateCategory(
                                id = categoryId,
                                name = name,
                                type = selectedType,
                                icon = icon
                            )
                        } else {
                            viewModel.addCategory(
                                com.example.moneytracker.data.local.entities.Category(
                                    name = name,
                                    type = selectedType,
                                    icon = icon
                                )
                            )
                        }
                        onBackClick()
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text(
                    if (categoryId != null) 
                        stringResource(R.string.update)
                    else 
                        stringResource(R.string.save)
                )
            }
        }
    }
}
