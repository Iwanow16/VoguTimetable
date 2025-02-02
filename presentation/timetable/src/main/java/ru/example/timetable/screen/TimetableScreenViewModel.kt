package ru.example.timetable.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.example.mvi.MviIntent
import ru.example.mvi.MviState
import ru.example.mvi.MviViewModel
import ru.example.mvi.utils.LceState
import ru.example.timetable.mapper.TimetableToUiMapper
import ru.example.timetable.models.TimetableUi
import ru.example.timetable.screen.navigation.TIMETABLE_ID_ARG
import ru.example.timetable.screen.navigation.TIMETABLE_TYPE_ARG
import ru.test.domain.usecase.timetable.GetTimetableUseCase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
internal class TimetableScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTimeTableUseCase: GetTimetableUseCase,
    private val timetableToUiMapper: TimetableToUiMapper
) : MviViewModel<TimetableScreenIntent, TimetableScreenState>() {

    private val timetableId: Int? = savedStateHandle[TIMETABLE_ID_ARG]
    private val timetableType: String? = savedStateHandle[TIMETABLE_TYPE_ARG]

    private var currentDate = LocalDate.now()

    private var isLastPage = false
    private var isLoading = false

    init {
        loadMore(false)
    }

    override fun initialStateCreator(): TimetableScreenState =
        TimetableScreenState(LceState.Loading)

    override fun handleIntent(intent: TimetableScreenIntent) {
        when (intent) {
            is TimetableScreenIntent.LoadMore -> loadMore(intent.isPaging)
        }
    }

    private fun loadMore(isPaging: Boolean) {
        if (timetableId == null || timetableType == null || isLoading || isLastPage) return

        isLoading = true

        viewModelScope.launch {

            val dateRange = getWeekRange(currentDate)
            currentDate = currentDate.plusWeeks(2)

            getTimeTableUseCase(
                timetableId = timetableId,
                timetableType = timetableType,
                dateStart = dateRange.first,
                dateEnd = dateRange.second,
                isPaging = isPaging
            )
                .onSuccess { timetable ->
                    val newTimetable = timetableToUiMapper.invoke(timetable)
                    val currentWeek = (uiState.value.screenData as? LceState.Content)?.content?.weeks
                        ?: emptyList()

                    if (newTimetable.weeks.isEmpty()) isLastPage = true

                    updateState {
                        it.copy(
                            screenData = LceState.Content(
                                TimetableUi(
                                    weeks = currentWeek + newTimetable.weeks,
                                    groupName = newTimetable.groupName,
                                    isOffline = newTimetable.isOffline
                                )
                            )
                        )
                    }
                }
                .onFailure { error ->
                    updateState { it.copy(screenData = LceState.Error(error)) }
                }
                .also {
                    isLoading = false
                }
        }
    }

    private fun getWeekRange(date: LocalDate): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val firstDayOfWeek = date.with(DayOfWeek.MONDAY)
        val lastDayOfWeek = date.plusWeeks(1).with(DayOfWeek.SUNDAY)

        return Pair(
            firstDayOfWeek.format(formatter),
            lastDayOfWeek.format(formatter)
        )
    }
}

internal sealed interface TimetableScreenIntent : MviIntent {
    data class LoadMore(val isPaging: Boolean) : TimetableScreenIntent
}

internal data class TimetableScreenState(
    val screenData: LceState<TimetableUi>
) : MviState