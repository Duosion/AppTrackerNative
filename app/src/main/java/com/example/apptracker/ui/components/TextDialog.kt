package com.example.apptracker.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.apptracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDialog(
    onDismissRequest: () -> Unit,
    titleText: Int,
    contentText: String,
    confirmText: Int,
    dismissText: Int,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            ResourceText(titleText)
        },
        text = {
            Text(contentText)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm

            ) {
                ResourceText(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                ResourceText(dismissText)
            }
        }
    )
}