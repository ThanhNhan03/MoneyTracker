package com.example.moneytracker.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
    onCategoryClick: (Category) -> Unit,
    onEditClick: (Category) -> Unit,
    onDeleteClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteMenu by remember { mutableStateOf(false) }

    LaunchedEffect(category.isDefault) {
        if (category.isDefault) {
            showDeleteMenu = false
        }
    }

    Card(
        onClick = { if (!category.isDefault) onEditClick(category) },
        modifier = modifier,
        enabled = !category.isDefault
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            if (category.icon.startsWith("content://") || category.icon.startsWith("file://")) {
                AsyncImage(
                    model = Uri.parse(category.icon),
                    contentDescription = stringResource(R.string.category_icon),
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = stringResource(R.string.category_icon),
                    modifier = Modifier.size(48.dp),
                    tint = if (category.type == "income") IncomeGreen else ExpenseRed
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Type
            Surface(
                color = if (category.type == "income") IncomeGreen else ExpenseRed,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (category.type == "expense") 
                        stringResource(R.string.expense)
                    else 
                        stringResource(R.string.income),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action Button (MoreVert / placeholder for default categories)
            Box(modifier = Modifier.size(32.dp)) { // Ensure consistent size
                if (!category.isDefault) {
                    IconButton(
                        onClick = { showDeleteMenu = true },
                        modifier = Modifier.fillMaxSize() // Fill the Box
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showDeleteMenu,
                        onDismissRequest = { showDeleteMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showDeleteMenu = false
                                onDeleteClick(category)
                            }
                        )
                    }
                }
                // No else branch: if not !category.isDefault, the Box still takes up space but is empty
            }
        }
    }
}
