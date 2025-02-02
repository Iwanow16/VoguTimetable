package ru.example.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SettingsScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        modifier = modifier,
        uiState = uiState,
        checkUpdate = {
            viewModel.postIntent(SettingsScreenIntent.CheckForUpdate)
        },
        downloadUpdate = { url ->
            viewModel.postIntent(SettingsScreenIntent.DownloadUpdate(url))
        },
        updateApp = { uri ->
            viewModel.postIntent(SettingsScreenIntent.InstallUpdate(uri))
        }
    )
}