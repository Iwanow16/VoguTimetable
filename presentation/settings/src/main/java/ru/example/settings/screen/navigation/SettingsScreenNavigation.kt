package ru.example.settings.screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.example.settings.screen.SettingsScreenRoute

const val SETTINGS_SCREEN_ROUTE = "settings_screen"

fun NavController.navigateToSettings() = navigate(SETTINGS_SCREEN_ROUTE) {
    popUpTo(SETTINGS_SCREEN_ROUTE) {
        inclusive = true
    }
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = SETTINGS_SCREEN_ROUTE) {
        SettingsScreenRoute()
    }
}