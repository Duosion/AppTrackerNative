package com.example.apptracker.ui.routes.settings.appearance

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.room.RoomDatabase
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.routes.settings.DialogListItem
import com.example.apptracker.ui.routes.settings.SettingsDialogListItemCard
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePage (
    navController: NavController,
    database: AppDatabase,
    viewModel: AppearanceViewModel = AppearanceViewModel(database)
) {
    val screenState by viewModel.state.collectAsState()

    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { Text(stringResource(id = R.string.settings_appearance_title)) },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        val darkModeSetting = screenState.darkModeValue
        val oledModeEnabled = screenState.oledModeValue == OledModeValues.ON
        val dynamicColorModeEnabled = screenState.dynamicColorModeValue == DynamicColorModeValues.ON

        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {

            SettingsDialogListItemCard(
                headlineText = { Text(stringResource(id = R.string.settings_dark_mode_setting_name)) },
                supportingText = {
                    Text(stringResource(id = darkModeSetting.valueName))
                },
                values = DarkModeValues.values().map {
                    DialogListItem(
                        name = it.valueName,
                        value = it.id
                    )
                },
                selectedValue = darkModeSetting.id,
                onSelect = {
                    viewModel.setDarkModeValue(it.value as Int)
                }
            )
            Divider()

            val isOledToggleEnabled = darkModeSetting != DarkModeValues.OFF
            SettingsListItemCard(
                headlineText = { Text(stringResource(id = R.string.settings_oled_mode_setting_name)) },
                enabled = isOledToggleEnabled,
                trailingContent = {
                    Switch(
                        checked = oledModeEnabled,
                        enabled = isOledToggleEnabled,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    viewModel.setOledModeValue(
                        id = if (oledModeEnabled) 0 else 1
                    )
                }
            )

            SettingsListItemCard(
                headlineText = { Text(stringResource(id = R.string.settings_dynamic_color_mode_setting_name)) },
                trailingContent = {
                    Switch(
                        checked = dynamicColorModeEnabled,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    viewModel.setDynamicColorModeValue(
                        id = if (dynamicColorModeEnabled) 0 else 1
                    )
                }
            )

        }
    }
}