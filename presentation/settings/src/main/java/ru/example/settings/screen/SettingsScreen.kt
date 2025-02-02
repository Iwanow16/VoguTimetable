package ru.example.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.example.settings.R
import ru.example.settings.components.SettingsTextItem
import ru.example.ui_kit.components.ErrorContent

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsScreenState,
    checkUpdate: () -> Unit,
    downloadUpdate: (String) -> Unit,
    updateApp: (String) -> Unit
) {
    SettingsScreenContent(
        version = uiState.currentVersion,
        updateStatus = uiState.updateStatus,
        checkUpdate = checkUpdate,
        downloadUpdate = downloadUpdate,
        updateApp = updateApp
    )
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    version: String,
    updateStatus: UpdateStatus,
    checkUpdate: () -> Unit,
    downloadUpdate: (String) -> Unit,
    updateApp: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingAppVersion(
            version = version,
            updateStatus = updateStatus,
            checkUpdate = checkUpdate,
            downloadUpdate = downloadUpdate,
            updateApp = updateApp,
        )
    }
}

@Composable
private fun SettingAppVersion(
    modifier: Modifier = Modifier,
    version: String,
    updateStatus: UpdateStatus,
    checkUpdate: () -> Unit,
    downloadUpdate: (String) -> Unit,
    updateApp: (String) -> Unit
) {
    when (updateStatus) {
        UpdateStatus.Idle ->
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_default, version),
                onClick = checkUpdate
            )

        UpdateStatus.Checking ->
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_checking),
                onClick = { }
            )

        UpdateStatus.NoUpdate ->
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_no_update),
                onClick = checkUpdate
            )

        is UpdateStatus.UpdateAvailable ->
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(
                    R.string.settings_description_new_version,
                    updateStatus.release.version
                ),
                onClick = {
                    downloadUpdate(updateStatus.release.downloadUrl)
                }
            )

        UpdateStatus.Downloading -> {
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_downloading),
                onClick = { }
            )
        }

        is UpdateStatus.ReadyToUpdate -> {
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_install),
                onClick = { updateApp(updateStatus.uri) }
            )
        }

        UpdateStatus.Installing -> {
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_install),
                onClick = { }
            )
        }

        UpdateStatus.Installed -> {
            SettingsTextItem(
                title = stringResource(R.string.settings_title_version),
                description = stringResource(R.string.settings_description_installed),
                onClick = checkUpdate
            )
        }

        is UpdateStatus.Error -> {
            ErrorContent(updateStatus.throwable.message.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {

}