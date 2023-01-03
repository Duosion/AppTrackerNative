package com.example.apptracker.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.apptracker.R
import com.example.apptracker.ui.routes.more.addApps.AddAppsViewQueryState
import com.example.apptracker.ui.routes.more.addApps.SortFunction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onSearch: (String) -> Unit,
    onSortModeChanged: (SortFunction) -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onNavigationIconClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    var sortDialogOpen by remember { mutableStateOf(false) }
    var selectedSortMode by remember { mutableStateOf(AddAppsViewQueryState().sortMode) }

    fun collapseSearch() {
        isSearchFocused = false
        searchQuery = "" // reset query
        onSearch(searchQuery)
    }

    if (isSearchFocused) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    if (sortDialogOpen) {
        AlertDialog(
            onDismissRequest = { sortDialogOpen = false },
            title = { Text(stringResource(id = R.string.search_sort_dialog_text)) },
            text = {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                ) {
                    SortFunction.values().forEach { option ->
                        ElevatedButton(
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                selectedSortMode = option
                                onSortModeChanged(option)
                                sortDialogOpen = false
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
                                    selected = option == selectedSortMode,
                                    onClick = null
                                )
                                Text(
                                    text = option.name,
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
                        sortDialogOpen = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    TopAppBar(
        title = {
            if (isSearchFocused) {
                val transparent = Color.Transparent
                val focusManager = LocalFocusManager.current
                BackHandler() {
                    collapseSearch()
                }
                TextField(
                    modifier = Modifier
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                isSearchFocused = true
                            }
                        }
                        .focusRequester(focusRequester),
                    value = searchQuery,
                    onValueChange = { newValue ->
                        searchQuery = newValue
                    },
                    singleLine = true,
                    placeholder = { Text( stringResource(id = R.string.search_textfield_placeholder) ) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = transparent,
                        unfocusedIndicatorColor = transparent,
                        containerColor = transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearch(searchQuery)
                        }
                    )
                )
            } else {
                title()
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = {
                    if (isSearchFocused) {
                        collapseSearch()

                    } else {
                        onNavigationIconClick()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = stringResource(id = R.string.back_button_description)
                )
            }
        },
        actions = {
            if (!isSearchFocused) {
                IconButton(
                    onClick = {
                        isSearchFocused = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = stringResource(id = R.string.search_textfield_placeholder)
                    )
                }
            }
            IconButton(
                onClick = {
                    sortDialogOpen = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sort_icon),
                    contentDescription = stringResource(id = R.string.search_sort_placeholder)
                )
            }
            actions()
        },
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )

}
