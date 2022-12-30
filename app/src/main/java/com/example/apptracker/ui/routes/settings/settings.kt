package com.example.apptracker.ui.routes.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ListItemCard
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.util.navigation.SettingsListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navController: NavController
) {
    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val listItems = listOf(
                    SettingsListItem.General,
                    SettingsListItem.Appearance
                )
                items(listItems) { item ->
                    SettingsListEntry(
                        navController = navController,
                        listItem = item
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsListEntry(
    navController: NavController,
    listItem: SettingsListItem
) {
    val headline = listItem.headline
    SettingsListItemCard(
        headlineText = { Text(stringResource(id = headline)) },
        supportingText = { Text(stringResource(id = listItem.supporting)) },
        leadingContent = { Icon(
            painter = painterResource(id = listItem.icon),
            contentDescription = stringResource(headline),
            tint = MaterialTheme.colorScheme.primary
        )},
        onClick = {
            navController.navigate(listItem.route.path) {
                /*popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }*/
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsListItemCard(
    enabled: Boolean = true,
    headlineText: @Composable () -> Unit,
    overlineText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    ListItemCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        headlineText = headlineText,
        overlineText = overlineText,
        supportingText = supportingText,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        onClick = onClick
    )
}

data class DialogListItem(
    val name: String,
    val value: Any
)

@Composable
fun SettingsDialogListItemCard(
    enabled: Boolean = true,
    headlineText: @Composable () -> Unit,
    overlineText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    values: List<DialogListItem> = listOf(),
    dialogTitle: Int,
    selectedValue: Int = 0,
    onSelect: (DialogListItem) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var dialogOpen by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(values[selectedValue]) }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { dialogOpen = false },
            title = { ResourceText(dialogTitle) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .selectableGroup()
                ) {
                    items(values) { option ->
                        ElevatedButton(
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onSelect(option)
                                selectedOption = option
                                dialogOpen = false
                            },
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            ),
                            shape = RectangleShape
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = option == selectedOption,
                                    onClick = null
                                )
                                Text(
                                    text =option.name,
                                    modifier = Modifier
                                        .padding(start = 10.dp),
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogOpen = false
                        onDismiss()
                    }
                ) {
                    ResourceText(R.string.settings_dialog_list_dismiss)
                }
            }
        )
    }

    SettingsListItemCard(
        enabled = enabled,
        headlineText = headlineText,
        overlineText = overlineText,
        supportingText = supportingText,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        onClick = {
            dialogOpen = true
        }
    )
}