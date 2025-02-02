package ru.example.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<Intent : MviIntent, State : MviState> :
    ViewModel() {

    abstract fun initialStateCreator(): State

    private val initialState: State by lazy { initialStateCreator() }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow()
    private val intent: SharedFlow<Intent> = _intent.asSharedFlow()

    init {
        subscribe()
    }

    private fun subscribe() {
        viewModelScope.launch { intent.collect { handleIntent(it) } }
    }

    protected abstract fun handleIntent(intent: Intent)

    protected fun updateState(update: (State) -> State) {
        _uiState.update(update)
    }

    fun postIntent(intent: Intent) {
        viewModelScope.launch { _intent.emit(intent) }
    }
}