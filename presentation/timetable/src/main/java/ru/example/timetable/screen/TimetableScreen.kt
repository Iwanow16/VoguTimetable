package ru.example.timetable.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import ru.example.mvi.utils.LceState
import ru.example.timetable.components.EmptyLessonItem
import ru.example.timetable.components.HeaderDayDate
import ru.example.timetable.components.HeaderWeekType
import ru.example.timetable.components.ItemLesson
import ru.example.timetable.models.DayUi
import ru.example.timetable.models.LessonUi
import ru.example.timetable.models.TimetableUi
import ru.example.timetable.models.WeekUi
import ru.example.ui_kit.components.ErrorContent
import ru.example.ui_kit.components.LoadingContent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun TimetableScreen(
    modifier: Modifier = Modifier,
    uiState: TimetableScreenState,
    onScroll: (Boolean) -> Unit
) {
    when (val state = uiState.screenData) {
        is LceState.Content -> TimetableScreenContent(
            timetable = state.content,
            onScroll = onScroll
        )

        is LceState.Error -> ErrorContent(
            state.throwable.message.toString()
        )

        LceState.Loading -> LoadingContent()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimetableScreenContent(
    modifier: Modifier = Modifier,
    timetable: TimetableUi,
    onScroll: (Boolean) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val todayDate = LocalDate.now().format(formatter)

    var headerHeight by remember { mutableIntStateOf(0) }
    val todayItemIndex = remember(timetable) {
        var index = 0
        var targetIndex = -1

        timetable.weeks.forEach { week ->
            index++

            week.days.forEach { day ->
                if (day.date == todayDate) {
                    targetIndex = index
                }
                index++
                index += if (day.lessons.isEmpty()) 1 else day.lessons.size
            }
        }
        targetIndex
    }

    LazyColumn(state = lazyListState) {
        timetable.weeks.forEach { week ->
            stickyHeader {
                HeaderWeekType(
                    type = week.type,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        headerHeight = coordinates.size.height
                    }
                )
            }

            week.days.forEach { day ->
                item {
                    HeaderDayDate(
                        date = day.date,
                        isToday = day.date == todayDate
                    )
                }

                if (day.lessons.isEmpty()) {
                    item { EmptyLessonItem() }
                } else {
                    items(day.lessons) { lesson ->
                        ItemLesson(lesson = lesson)
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem =
                    lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size

                if (totalItems > 0 && lastVisibleItem >= totalItems - 3) {
                    onScroll(true)
                }
            }
    }

    LaunchedEffect(todayItemIndex, headerHeight) {
        if (todayItemIndex != -1) {
            lazyListState.animateScrollToItem(
                index = todayItemIndex,
                scrollOffset = -headerHeight
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TimetableScreenContentPreview() {
    val groupName = "TEST-0000"

    val lesson = LessonUi(
        time = "15:20 - 16:50",
        type = "TEST",
        location = "TEST",
        teacher = "TEST",
        subject = "TEST",
        subgroup = "TEST",
        group = groupName,
        id = 0
    )

    val day = DayUi(
        lessons = listOf(lesson, lesson.copy(time = "17:00 - 18:30")),
        date = "04.02.2025"
    )

    val week = WeekUi(
        days = listOf(day, day),
        type = "Нечетная неделя"
    )

    val timetable = TimetableUi(
        weeks = listOf(week),
        groupName = groupName,
        isOffline = true
    )

    TimetableScreenContent(
        timetable = timetable,
        onScroll = {}
    )
}