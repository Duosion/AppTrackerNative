package com.example.apptracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    inputMaxLength: Int = 0,
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    var textFieldValue by remember { mutableStateOf(defaultValue) }
    var textFieldError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            ResourceText(titleText)
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldError = false
                        if (inputMaxLength >= it.length) textFieldValue = it
                    },
                    isError = textFieldError,
                    label = {
                        ResourceText(labelText)
                    },
                    placeholder = {
                        ResourceText(placeholderText)
                    },
                    singleLine = true
                )
                Row (
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    if (textFieldError) {
                        ResourceText(
                            modifier = Modifier.fillMaxWidth(.5f),
                            id = errorText,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                    if (inputMaxLength > 0) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.component_text_field_max_length).format(textFieldValue.length, inputMaxLength),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
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