package ru.test.presentation.screen.timetable

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.test.presentation.component.timetable.EmptyLessonItem
import ru.test.presentation.component.timetable.HeaderDayDate
import ru.test.presentation.component.timetable.HeaderWeekType
import ru.test.presentation.component.timetable.ItemLesson
import ru.test.presentation.models.WeekUi
import ru.test.presentation.utils.State
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel<TimetableViewModel>(),
    onSetTopBarTitle: (String) -> Unit = {},
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
            val data = currentState.data

            if (data.isOffline) {
                onSetTopBarTitle("Оффлайн просмотр: \n ${data.groupName}")
            } else {
                onSetTopBarTitle(data.groupName)
            }

            if (data.weeks.isNotEmpty()) {
                Timetable(timetable = data.weeks)
            } else {
                EmptyTimetable()
            }
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

            week.days.forEach { day ->
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

@Preview(showBackground = true)
@Composable
fun EmptyTimetable() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "T-T", fontSize = 128.sp)

        Text(
            text = "Упс! Расписание не подгрузилось. Попробуй еще раз.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
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