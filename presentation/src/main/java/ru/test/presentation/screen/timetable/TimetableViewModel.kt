package ru.test.presentation.screen.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.test.domain.usecase.GetGroupTimeTableUseCase
import ru.test.presentation.mappers.WeekToUiMapper
import ru.test.presentation.models.WeekUi
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val getGroupTimeTableUseCase: GetGroupTimeTableUseCase,
    private val weekToUiMapper: WeekToUiMapper

) : ViewModel() {

    private val _weeks: MutableStateFlow<List<WeekUi>> = MutableStateFlow(emptyList())
    val weeks: StateFlow<List<WeekUi>> = _weeks.asStateFlow()

    fun getTimetable(groupId: Int) {
        viewModelScope.launch {
            _weeks.value = getGroupTimeTableUseCase(groupId)
                .map { weekToUiMapper.invoke(it) }
        }
    }
}