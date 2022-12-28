package com.example.apptracker.ui.routes.settings.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.routes.settings.DialogListItem
import com.example.apptracker.ui.routes.settings.SettingsDialogListItemCard
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import com.example.apptracker.util.data.settings.values.ThemeValues

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
        val themeSetting = screenState.themeValue
        val oledModeEnabled = screenState.oledModeValue == OledModeValues.ON

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
            SettingsDialogListItemCard(
                headlineText = { Text(stringResource(id = R.string.settings_theme_setting_name)) },
                supportingText = {
                    Text(stringResource(id = themeSetting.valueName))
                },
                values = ThemeValues.values().map {
                    DialogListItem(
                        name = it.valueName,
                        value = it.id
                    )
                },
                selectedValue = themeSetting.id,
                onSelect = {
                    viewModel.setThemeValue(it.value as Int)
                }
            ) {

            }
            val isOledToggleEnabled = darkModeSetting != DarkModeValues.OFF
            if (isOledToggleEnabled) {
                Divider()
                SettingsListItemCard(
                    headlineText = { Text(stringResource(id = R.string.settings_oled_mode_setting_name)) },
                    trailingContent = {
                        Switch(
                            checked = oledModeEnabled,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        viewModel.setOledModeValue(
                            id = if (oledModeEnabled) 0 else 1
                        )
                    }
                )
            }

        }
    }
}