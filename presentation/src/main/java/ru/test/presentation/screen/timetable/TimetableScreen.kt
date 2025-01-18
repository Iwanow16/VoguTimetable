package ru.test.presentation.screen.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ru.test.presentation.component.EmptyLessonItem
import ru.test.presentation.component.HeaderDayDate
import ru.test.presentation.component.HeaderWeekType
import ru.test.presentation.component.ItemLesson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableScreen(
    modifier: Modifier = Modifier,
    viewModel: TimetableViewModel = hiltViewModel<TimetableViewModel>(),
    onBackPressed: () -> Unit = {}
) {
    val weeks = viewModel.weeks.collectAsState(initial = emptyList()).value

    val lazyListState = rememberLazyListState()

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val todayDate = LocalDate.now().format(formatter)

    val todayIndex = remember(weeks, todayDate) {
        var index = 0
        weeks.forEach { week ->
            index++
            for (day in week.days) {
                if (day.value.date == todayDate) {
                    return@remember index
                }
                index += 1 + (if (day.value.lessons.isEmpty()) 1 else day.value.lessons.size)
            }
        }
        0
    }

    LaunchedEffect(todayIndex) {
        if (todayIndex > 0) {
            lazyListState.animateScrollToItem(todayIndex - 1)
        }
    }

    LaunchedEffect(todayDate) {
        lazyListState.scrollToItem(8)
    }

    LazyColumn(state = lazyListState) {
        weeks.forEach { week ->
            stickyHeader {
                HeaderWeekType(type = week.type)
            }

            week.days.forEach { (_, day) ->
                item {
                    HeaderDayDate(date = day.date, isToday = day.date == todayDate)
                }

                if (day.lessons.isEmpty()) {
                    item {
                        EmptyLessonItem()
                    }
                } else {
                    items(day.lessons) { lesson ->
                        ItemLesson(lesson = lesson)
                    }
                }
            }
        }
    }
}