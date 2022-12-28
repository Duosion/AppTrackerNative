package com.example.apptracker.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.apptracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldDialog(
    onDismissRequest: () -> Unit,
    titleText: Int,
    errorText: Int = R.string.text_field_empty_content_error,
    defaultValue: String = "",
    confirmText: Int,
    dismissText: Int,
    placeholderText: Int,
    labelText: Int,
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var textFieldValue by remember { mutableStateOf(defaultValue) }
    var textFieldError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            ResourceText(titleText)
        },
        text = {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                isError = textFieldError,
                label = {
                    ResourceText(labelText)
                },
                placeholder = {
                    ResourceText(placeholderText)
                },
                supportingText = {
                    if (textFieldError) {
                        ResourceText(errorText)
                    }
                },
                maxLines = 1
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (textFieldValue == "") {
                        textFieldError = true
                    } else {
                        onConfirm(textFieldValue)
                        //onDismissRequest()
                    }
                }
            ) {
                ResourceText(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    //onDismissRequest()
                }
            ) {
                ResourceText(dismissText)
            }
        }
    )
}