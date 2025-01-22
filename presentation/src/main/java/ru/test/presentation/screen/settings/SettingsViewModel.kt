package ru.test.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.test.domain.models.update.Release
import ru.test.domain.usecase.update.CheckUpdateUseCase
import ru.test.domain.usecase.update.DownloadUpdateUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val checkUpdateUseCase: CheckUpdateUseCase,
    private val downloadUpdateUseCase: DownloadUpdateUseCase
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            checkUpdateUseCase()
                .onSuccess { status ->
                    _updateState.value = if (status.isAvailable) {
                        UpdateState.UpdateAvailable(status.release!!)
                    } else {
                        UpdateState.NoUpdate
                    }
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message)
                }
        }
    }

    fun downloadUpdate(downloadUrl: String) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            downloadUpdateUseCase(downloadUrl)
                .onSuccess { uri ->
                    _updateState.value = UpdateState.Ready(uri)
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message)
                }
        }
    }
}

sealed class UpdateState {
    data object Initial : UpdateState()
    data object Loading : UpdateState()
    data object NoUpdate : UpdateState()
    data object Downloading : UpdateState()
    data class UpdateAvailable(val release: Release) : UpdateState()
    data class Ready(val uri: String) : UpdateState()
    data class Error(val message: String?) : UpdateState()
}