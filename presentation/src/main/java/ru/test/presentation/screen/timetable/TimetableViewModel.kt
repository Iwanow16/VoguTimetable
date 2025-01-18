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
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val getGroupTimeTableUseCase: GetTimeTableUseCase,
    private val weekToUiMapper: WeekToUiMapper

) : ViewModel() {

    private val _weeks: MutableStateFlow<List<WeekUi>> = MutableStateFlow(emptyList())
    val weeks: StateFlow<List<WeekUi>> = _weeks.asStateFlow()

    init {
        try {
            viewModelScope.launch {
                _weeks.value = getGroupTimeTableUseCase()
                    .map { weekToUiMapper.invoke(it) }
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }
}