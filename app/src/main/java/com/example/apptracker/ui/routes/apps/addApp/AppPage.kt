package com.example.apptracker.ui.routes.apps.addApp

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.components.TextDialog
import com.example.apptracker.ui.routes.settings.DialogListItem
import com.example.apptracker.ui.routes.settings.SettingsDialogListItemCard
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppReminderOffset
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppPage(
    navController: NavController,
    database: AppDatabase,
    appInfo: ApplicationInfo,
    mode: AppPageMode = AppPageMode.ADD,
    viewModel: IAppPageViewModel = AddAppViewModel(database, appInfo.packageName)
) {
    val defaultTrackedApp = TrackedApp(appInfo.packageName)

    val screenState by viewModel.state.collectAsState()
    val trackedApp by screenState.trackedApp.collectAsState(initial = defaultTrackedApp)

    val packageManager = LocalContext.current.packageManager
    val appLabel = appInfo.loadLabel(packageManager).toString()

    var timePickerEnabled by remember { mutableStateOf(false) }
    var customReminderOffsetTimePickerEnabled by remember { mutableStateOf(false) }
    var deleteConfirmDialogEnabled by remember { mutableStateOf(false) }

    val categories = screenState.categories

    val dayStart = LocalTime.of(
        trackedApp?.dayStartHour ?: defaultTrackedApp.dayStartHour,
        trackedApp?.dayStartMinute ?: defaultTrackedApp.dayStartMinute
    )
    val customOffsetTime = LocalTime.of(
        trackedApp?.reminderOffsetHour ?: defaultTrackedApp.reminderOffsetHour,
        trackedApp?.reminderOffsetMinute ?: defaultTrackedApp.reminderOffsetMinute
    )

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
        customReminderOffsetTimePickerEnabled -> {
            TimePickerDialog(
                onDismissRequest = { customReminderOffsetTimePickerEnabled = false },
                onTimeChange = {
                    viewModel.setCustomReminderOffsetTime(it)
                    customReminderOffsetTimePickerEnabled = false
                },
                initialTime = customOffsetTime,
                title = { ResourceText(id = R.string.apps_add_app_reminder_offset_custom_offset_dialog_title) }
            )
        }
        deleteConfirmDialogEnabled -> {
            TextDialog(
                onDismissRequest = { deleteConfirmDialogEnabled = false },
                titleText = R.string.apps_edit_app_delete_confirm_dialog_title,
                contentText = stringResource(id = R.string.apps_edit_app_delete_confirm_dialog_content),
                confirmText = R.string.apps_edit_app_delete_confirm_dialog_confirm,
                dismissText = R.string.apps_edit_app_delete_confirm_dialog_cancel,
                onConfirm = {
                    viewModel.deleteTrackedApp()
                    deleteConfirmDialogEnabled = false
                    navController.popBackStack()
                },
                onDismiss = { deleteConfirmDialogEnabled = false }
            )
        }
    }

    if (screenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        Scaffold(
            topBar = {
                BackTopAppBar(
                    title = {
                        ResourceText(
                            id = if (mode == AppPageMode.ADD) R.string.apps_add_app_title else R.string.apps_edit_app_title
                        )
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    actions = {
                        if (mode == AppPageMode.EDIT) {
                            IconButton(
                                onClick = { deleteConfirmDialogEnabled = true }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete_icon),
                                    contentDescription = stringResource(id = R.string.apps_edit_app_delete_confirm_dialog_title)
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (mode == AppPageMode.ADD) {
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
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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
                val dayStartIsUTC = trackedApp?.dayStartIsUTC ?: defaultTrackedApp.dayStartIsUTC
                SettingsListItemCard(
                    headlineText = { ResourceText(id = R.string.apps_add_app_day_start_utc_headline) },
                    trailingContent = {
                        Switch(
                            checked = dayStartIsUTC,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        viewModel.setDayStartIsUTC(dayStartIsUTC)
                    }
                )
                Divider()
                // reminder notification stuff
                val reminderNotification = trackedApp?.reminderNotification ?: defaultTrackedApp.reminderNotification
                SettingsListItemCard(
                    headlineText = { ResourceText(id = R.string.apps_add_app_reminder_notification_headline) },
                    supportingText = { ResourceText(id = R.string.apps_add_app_reminder_notification_supporting) },
                    trailingContent = {
                        Switch(
                            checked = reminderNotification,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        viewModel.setReminderNotification(!reminderNotification)
                    }
                )
                if (reminderNotification) {
                    val currentOffset = TrackedAppReminderOffset.fromId(trackedApp?.reminderOffset ?: defaultTrackedApp.reminderOffset)
                    // show offset picker
                    SettingsDialogListItemCard(
                        headlineText = { ResourceText(id = R.string.apps_add_app_reminder_offset_headline) },
                        dialogTitle = R.string.apps_add_app_reminder_offset_headline_dialog_name,
                        supportingText = {
                            ResourceText(currentOffset.valueName)
                        },
                        values = TrackedAppReminderOffset.values().map {
                            DialogListItem(
                                name = stringResource(id = it.valueName),
                                value = it.id
                            )
                        },
                        selectedValue = currentOffset.id,
                        onSelect = {
                            viewModel.setReminderOffset(it.value as Int)
                        }
                    )
                    if (currentOffset == TrackedAppReminderOffset.CUSTOM) {

                        SettingsListItemCard(
                            headlineText = { ResourceText(id = R.string.apps_add_app_reminder_offset_custom_offset_headline) },
                            supportingText = {
                                Text(
                                    customOffsetTime.format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.apps_add_app_day_start_time_pattern)
                                        )
                                    )
                                )
                            },
                            onClick = {
                                customReminderOffsetTimePickerEnabled = true
                            }
                        )
                    }
                }
                Divider()
                if (categories.isNotEmpty()) {
                    val trackedAppCatId = trackedApp?.categoryId ?: defaultTrackedApp.categoryId
                    val selectedCategory = categories.find { it.id == trackedAppCatId } ?: categories.first()
                    println(selectedCategory.name + " " + selectedCategory.position)
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
}