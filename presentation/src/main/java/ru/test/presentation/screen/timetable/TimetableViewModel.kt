package ru.test.presentation.screen.timetable

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.test.domain.usecase.GetTimeTableUseCase
import ru.test.presentation.mappers.WeekToUiMapper
import ru.test.presentation.models.WeekUi
import ru.test.presentation.utils.State
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val getTimeTableUseCase: GetTimeTableUseCase,
    private val weekToUiMapper: WeekToUiMapper

) : ViewModel() {

    private val _state: MutableStateFlow<State<List<WeekUi>>> = MutableStateFlow(State.Loading)
    val state: StateFlow<State<List<WeekUi>>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val weeks = getTimeTableUseCase()
                    .map { weekToUiMapper.invoke(it) }
                _state.value = State.Success(weeks)
            } catch (e: Exception) {
                _state.value = State.Error("$e.message")
                Log.e("ERROR_TIMETABLE", "${e.message}")
            }
        }
    }
}