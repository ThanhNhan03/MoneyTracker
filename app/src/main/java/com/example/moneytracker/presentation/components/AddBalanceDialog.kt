package com.example.moneytracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import java.text.NumberFormat
import java.util.*

@Composable
fun AddBalanceDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
    errorMessage: String
) {
    var balanceAmount by remember { mutableStateOf("") }
    var balanceError by remember { mutableStateOf<String?>(null) }
    
    val format = NumberFormat.getNumberInstance(Locale.getDefault())
    val formattedAmount = try {
        if (balanceAmount.isNotEmpty()) {
            format.format(balanceAmount.replace(".", "").toLong())
        } else ""
    } catch (e: Exception) {
        balanceAmount 
    }

    AlertDialog(
        onDismissRequest = { 
            onDismiss()
            balanceAmount = ""
            balanceError = null
        },
        title = { Text(stringResource(R.string.add_balance)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.amount),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formattedAmount + " đ",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = balanceAmount,
                    onValueChange = { newValue ->
                        val cleanedValue = newValue.replace(".", "")
                        if (cleanedValue.matches(Regex("^\\d*$"))) {
                            balanceAmount = cleanedValue
                            balanceError = null
                        }
                    },
                    label = { Text(stringResource(R.string.add_balance)) },
                    singleLine = true,
                    isError = balanceError != null,
                    supportingText = {
                        if (balanceError != null) {
                            Text(balanceError!!)
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val quickAmounts = listOf(100000.0, 200000.0, 500000.0, 1000000.0, 2000000.0, 5000000.0)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    quickAmounts.forEach { amount ->
                        OutlinedButton(
                            onClick = {
                                balanceAmount = amount.toLong().toString()
                                balanceError = null
                            },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = format.format(amount),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val amount = balanceAmount.replace(".", "").toLongOrNull()
                        if (amount == null || amount <= 0) {
                            balanceError = errorMessage
                            return@TextButton
                        }
                        
                        onSave(amount.toDouble())
                        
                        onDismiss()
                        balanceAmount = ""
                        balanceError = null
                    } catch (e: Exception) {
                        balanceError = errorMessage
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { 
                    onDismiss()
                    balanceAmount = ""
                    balanceError = null
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.cancel))
            }
        }
    )
} 