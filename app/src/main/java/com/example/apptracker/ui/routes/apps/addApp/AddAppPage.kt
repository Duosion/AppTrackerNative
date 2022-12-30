package com.example.apptracker.ui.routes.apps.addApp

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.routes.settings.DialogListItem
import com.example.apptracker.ui.routes.settings.SettingsDialogListItemCard
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.data.AppDatabase
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppPage(
    navController: NavController,
    database: AppDatabase,
    packageManager: PackageManager,
    appInfo: ApplicationInfo,
    viewModel: AddAppViewModel = AddAppViewModel(database, appInfo.packageName)
) {
    val screenState by viewModel.state.collectAsState()
    val appLabel = appInfo.loadLabel(packageManager).toString()

    var timePickerEnabled by remember { mutableStateOf(false) }

    val categories = screenState.categories

    val trackedApp = screenState.trackedApp
    val dayStart = LocalTime.of(trackedApp.dayStartHour, trackedApp.dayStartMinute)

    when {
        timePickerEnabled -> {
            TimePickerDialog(
                onDismissRequest = { timePickerEnabled = false },
                onTimeChange = {
                    viewModel.setDayStartTime(it)
                    timePickerEnabled = false
                },
                initialTime = dayStart,
                title = { ResourceText(id = R.string.apps_add_app_day_start_dialog_title) }
            )
        }
    }

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = { ResourceText(R.string.apps_add_app_title) },
                onBack = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.addTrackedApp()
                    navController.popBackStack()
                }
            ) {
                ResourceText(id = R.string.apps_add_app_submit_button)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // app icon
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            Image(
                modifier = Modifier.size(60.dp),
                painter = rememberDrawablePainter(drawable = appInfo.loadIcon(packageManager)),
                contentDescription = appLabel,
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.padding(bottom = 5.dp))
            // app label
            Text(
                text = appLabel,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))

            SettingsListItemCard(
                headlineText = { ResourceText(id = R.string.apps_add_app_day_start_headline) },
                supportingText = {
                    Text(dayStart.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.apps_add_app_day_start_time_pattern))))
                },
                onClick = {
                    timePickerEnabled = true
                }
            )
            SettingsListItemCard(
                headlineText = { Text(stringResource(id = R.string.apps_add_app_day_start_utc_headline)) },
                trailingContent = {
                    Switch(
                        checked = trackedApp.dayStartIsUTC,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    viewModel.setDayStartIsUTC(!trackedApp.dayStartIsUTC)
                }
            )
            Divider()
            if (categories.isNotEmpty()) {
                val selectedCategory = categories.find { it.id == trackedApp.categoryId } ?: categories.first()

                SettingsDialogListItemCard(
                    dialogTitle = R.string.apps_add_app_category_dialog_headline,
                    headlineText = { ResourceText(id = R.string.apps_add_app_category_headline) },
                    supportingText = {
                        Text(selectedCategory.name)
                    },
                    values = categories.map {
                        DialogListItem(
                            name = it.name,
                            value = it.id
                        )
                    },
                    selectedValue = selectedCategory.position,
                    onSelect = {
                        viewModel.setCategoryId(it.value as Int)
                    }
                )
            }
        }
    }
}