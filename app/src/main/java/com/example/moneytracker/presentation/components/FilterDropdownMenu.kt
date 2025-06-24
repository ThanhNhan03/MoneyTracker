package com.example.moneytracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun FilterDropdownMenu(
    expanded: Boolean,
    selectedFilter: String,
    onDismissRequest: () -> Unit,
    onFilterSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("Tất cả") },
            onClick = {
                onFilterSelected("all")
            },
            leadingIcon = {
                if (selectedFilter == "all") {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Thu nhập") },
            onClick = {
                onFilterSelected("income")
            },
            leadingIcon = {
                if (selectedFilter == "income") {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Chi tiêu") },
            onClick = {
                onFilterSelected("expense")
            },
            leadingIcon = {
                if (selectedFilter == "expense") {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        )
    }
}
