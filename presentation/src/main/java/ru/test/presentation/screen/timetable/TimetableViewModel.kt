package ru.test.presentation.screen.timetable

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.test.domain.usecase.timetable.GetTimetableUseCase
import ru.test.presentation.mappers.TimetableToUiMapper
import ru.test.presentation.models.TimetableUi
import ru.test.presentation.utils.State
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val getTimeTableUseCase: GetTimetableUseCase,
    private val timetableToUiMapper: TimetableToUiMapper

) : ViewModel() {

    private val _state: MutableStateFlow<State<TimetableUi>> = MutableStateFlow(State.Loading)
    val state: StateFlow<State<TimetableUi>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val timetable = getTimeTableUseCase()
                _state.value = State.Success(timetableToUiMapper.invoke(timetable))
            } catch (e: Exception) {
                _state.value = State.Error("$e.message")
                Log.e("ERROR_TIMETABLE", "${e.message}")
            }
        }
    }
}