package com.example.apptracker.ui.routes

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.SearchTopAppBar
import com.example.apptracker.util.apps.AppsViewModelV2
import com.example.apptracker.util.apps.SortFunction
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAppsPage(
    navController: NavController,
    viewModel: AppsViewModelV2 = AppsViewModelV2( LocalContext.current.packageManager)
) {
    val screenState by viewModel.state.collectAsState()

    fun search(query: String) {
        viewModel.setQueryString(query)
    }

    fun sort(sortMode: SortFunction) {
        viewModel.setSortMode(sortMode)
    }

    Scaffold(
        topBar = {
            SearchTopAppBar(
                title = { Text(stringResource(id = R.string.more_add_app_button_headline)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = stringResource( id = R.string.back_button_description ))
                    }
                },
                onSearch = { query ->
                    search(query)
                },
                onSortModeChanged = { sortMode ->
                    sort(sortMode)
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
            Divider()
            if (screenState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                screenState.apps?.let {
                    val sortMode = screenState.queryState.sortMode
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(it) { info ->
                            val label = info.loadLabel(packageManager).toString()
                            AddAppListEntry(
                                appInfo = info,
                                packageManager = packageManager,
                                label = label,
                                sortMode = sortMode,
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
    sortMode: SortFunction = SortFunction.Name,
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
            supportingText = {
                when(sortMode) {
                    SortFunction.Size -> {
                        val file = File(appInfo.publicSourceDir)
                        val sizeInMB = file.length() / 1e+6F
                        Text("%.1f MB".format(sizeInMB))
                    }
                    else -> {
                        val packageName = appInfo.packageName
                        if (packageName != label) {
                            Text(packageName)
                        }
                    }
                }
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