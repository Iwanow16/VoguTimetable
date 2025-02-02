package ru.example.settings.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.example.mvi.MviIntent
import ru.example.mvi.MviState
import ru.example.mvi.MviViewModel
import ru.test.domain.models.update.Release
import ru.test.domain.usecase.update.CheckUpdateUseCase
import ru.test.domain.usecase.update.CleanupUpdateUseCase
import ru.test.domain.usecase.update.DownloadUpdateUseCase
import ru.test.domain.usecase.update.GetCurrentVersionUseCase
import ru.test.domain.usecase.update.InstallUpdateUseCase
import javax.inject.Inject

@HiltViewModel
internal class SettingsScreenViewModel @Inject constructor(
    private val checkUpdateUseCase: CheckUpdateUseCase,
    private val downloadUpdateUseCase: DownloadUpdateUseCase,
    private val getCurrentVersionUseCase: GetCurrentVersionUseCase,
    private val installUpdateUseCase: InstallUpdateUseCase,
    private val cleanupUpdateUseCase: CleanupUpdateUseCase
) :
    MviViewModel<SettingsScreenIntent, SettingsScreenState>() {

    init {
        getCurrentAppVersion()
    }

    override fun initialStateCreator(): SettingsScreenState =
        SettingsScreenState()

    override fun handleIntent(intent: SettingsScreenIntent) {
        when (intent) {
            SettingsScreenIntent.CheckForUpdate -> checkForUpdate()
            is SettingsScreenIntent.DownloadUpdate -> downloadUpdate(intent.url)
            is SettingsScreenIntent.InstallUpdate -> installUpdate(intent.uri)
        }
    }

    private fun getCurrentAppVersion() {
        viewModelScope.launch {
            val version = getCurrentVersionUseCase()
            updateState { it.copy(currentVersion = version) }
        }
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            updateState { it.copy(updateStatus = UpdateStatus.Checking) }

            checkUpdateUseCase()
                .onSuccess { status ->
                    if (status.isAvailable) {
                        updateState {
                            it.copy(updateStatus = UpdateStatus.UpdateAvailable(status.release!!))
                        }
                    } else {
                        updateState { it.copy(updateStatus = UpdateStatus.NoUpdate) }
                    }
                }
                .onFailure { error ->
                    updateState { it.copy(updateStatus = UpdateStatus.Error(error)) }
                }
        }
    }

    private fun downloadUpdate(downloadUrl: String) {
        viewModelScope.launch {
            updateState { it.copy(updateStatus = UpdateStatus.Downloading) }

            downloadUpdateUseCase(downloadUrl)
                .onSuccess { uri ->
                    updateState { it.copy(updateStatus = UpdateStatus.ReadyToUpdate(uri)) }
                }
                .onFailure { error ->
                    updateState { it.copy(updateStatus = UpdateStatus.Error(error)) }
                }
        }
    }

    private fun installUpdate(uri: String) {
        viewModelScope.launch {
            updateState { it.copy(updateStatus = UpdateStatus.Installing) }

            installUpdateUseCase(uri)
                .onSuccess {
                    updateState { it.copy(updateStatus = UpdateStatus.Installed) }
                    cleanupUpdateUseCase()
                }
                .onFailure { error ->
                    updateState { it.copy(updateStatus = UpdateStatus.Error(error)) }
                }
        }
    }
}

internal sealed interface SettingsScreenIntent : MviIntent {
    object CheckForUpdate : SettingsScreenIntent
    data class DownloadUpdate(val url: String) : SettingsScreenIntent
    data class InstallUpdate(val uri: String) : SettingsScreenIntent
}

internal data class SettingsScreenState(
    val currentVersion: String = "",
    val updateStatus: UpdateStatus = UpdateStatus.Idle
) : MviState

internal sealed interface UpdateStatus {
    object Idle : UpdateStatus
    object Checking : UpdateStatus
    object NoUpdate : UpdateStatus
    data class UpdateAvailable(val release: Release) : UpdateStatus // <-- TODO release to ui
    object Downloading : UpdateStatus
    data class ReadyToUpdate(val uri: String) : UpdateStatus
    object Installing : UpdateStatus
    object Installed : UpdateStatus
    data class Error(val throwable: Throwable) : UpdateStatus
}