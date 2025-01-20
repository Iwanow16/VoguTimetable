package ru.test.presentation.screen.timetable

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.test.presentation.component.EmptyLessonItem
import ru.test.presentation.component.HeaderDayDate
import ru.test.presentation.component.HeaderWeekType
import ru.test.presentation.component.ItemLesson
import ru.test.presentation.models.WeekUi
import ru.test.presentation.utils.State
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel<TimetableViewModel>(),
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is State.Error -> {
            Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
        }

        State.Loading -> {
            Loading()
        }

        is State.Success -> {
            Timetable(timetable = currentState.data)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Timetable(timetable: List<WeekUi>) {
    val lazyListState = rememberLazyListState()

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val todayDate = LocalDate.now().format(formatter)

    var headerHeight by remember { mutableIntStateOf(0) }
    val todayItemIndex = remember(timetable) {
        var index = 0
        var targetIndex = -1

        timetable.forEach { week ->
            index++

            week.days.forEach { (_, day) ->
                if (day.date == todayDate) {
                    targetIndex = index
                }
                index++
                index += if (day.lessons.isEmpty()) 1 else day.lessons.size
            }
        }
        targetIndex
    }

    LaunchedEffect(todayItemIndex, headerHeight) {
        if (todayItemIndex != -1) {
            lazyListState.animateScrollToItem(
                index = todayItemIndex,
                scrollOffset = -headerHeight
            )
        }
    }

    LazyColumn(state = lazyListState) {
        timetable.forEach { week ->
            stickyHeader {
                HeaderWeekType(
                    type = week.type,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        headerHeight = coordinates.size.height
                    }
                )
            }

            week.days.forEach { (_, day) ->
                item {
                    HeaderDayDate(date = day.date, isToday = day.date == todayDate)
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
}

@Composable
fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}