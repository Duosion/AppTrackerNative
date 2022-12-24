package com.example.apptracker.routes

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.SearchView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.apps.AppsViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAppsPage(
    navController: NavController,
    viewModel: AppsViewModel = AppsViewModel( LocalContext.current.packageManager )
) {
    val installedApps = viewModel.getApps()
    val focusRequester = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    if (isSearchFocused) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    fun collapseSearch() {
        isSearchFocused = false
        searchQuery = "" // reset query
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSearchFocused) {
                        val transparent = Color.Transparent
                        BackHandler() {
                            collapseSearch()
                        }
                        TextField(
                            modifier = Modifier
                                .onFocusChanged { state ->
                                    isSearchFocused = state.isFocused
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
                            )
                        )
                    } else {
                        Text(stringResource(id = R.string.more_add_app_button_headline))
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
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isSearchFocused) {
                                collapseSearch()
                            } else {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = stringResource( id = R.string.back_button_description ))
                    }
                }
            )
        }
    ) { padding ->
        val packageManager = LocalContext.current.packageManager
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /*var searchQuery by remember { mutableStateOf("") }
            var isFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        isFocused = state.isFocused
                    },
                value = searchQuery,
                placeholder = {
                    Text(stringResource(id = R.string.search_textfield_placeholder))
                },
                onValueChange = { text ->
                    searchQuery = text
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )*/
            if (installedApps.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(installedApps) { info ->
                        val label = info.loadLabel(packageManager).toString()
                        if (searchQuery == "" || label.contains(other = searchQuery, ignoreCase = true)) {
                            AddAppListEntry(
                                appInfo = info,
                                packageManager = packageManager,
                                label = label,
                                onClick = {

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppListEntry(
    appInfo: ApplicationInfo,
    packageManager: PackageManager,
    label: String = appInfo.loadLabel(packageManager).toString(),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        ListItem(
            modifier = Modifier.fillMaxSize(),
            headlineText = {
                Text(label)
            },
            leadingContent = {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = rememberDrawablePainter(drawable = appInfo.loadIcon(packageManager)) ,
                    contentDescription = label,
                    contentScale = ContentScale.FillHeight,

                )
            },
        )
    }
}