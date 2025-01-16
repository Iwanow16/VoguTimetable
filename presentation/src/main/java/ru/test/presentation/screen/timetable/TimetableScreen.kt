package ru.test.presentation.screen.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ru.test.presentation.component.EmptyLessonItem
import ru.test.presentation.component.HeaderDayDate
import ru.test.presentation.component.HeaderWeekType
import ru.test.presentation.component.ItemLesson
import ru.test.presentation.models.DayUi
import ru.test.presentation.models.LessonUi
import ru.test.presentation.models.WeekUi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableScreen(
    modifier: Modifier = Modifier,
    viewModel: TimetableViewModel = hiltViewModel<TimetableViewModel>(),
    onBackPressed: () -> Unit = {}
) {
    val weeks = viewModel.weeks.collectAsState(initial = emptyList()).value

    LazyColumn {
        weeks.forEach { week ->
            stickyHeader {
                HeaderWeekType(type = week.type)
            }

            week.days.forEach { (_, day) ->
                item {
                    HeaderDayDate(date = day.date)
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

@Preview(showBackground = true)
@Composable
fun PreviewTimetableScreen() {
    val previewWeeks = listOf(
        WeekUi(
            type = "Чётная неделя",
            days = mapOf(
                1 to DayUi(
                    date = "Понедельник, 15 января",
                    lessons = listOf(
                        LessonUi(
                            time = "9:00 - 10:30",
                            type = "Лекция",
                            location = "А-100",
                            teacher = "Иванов И.И.",
                            subject = "Высшая математика",
                            subgroup = "",
                            group = "БПИ-228",
                            id = 1
                        ),
                        LessonUi(
                            time = "10:45 - 12:15",
                            type = "Практика",
                            location = "Б-300",
                            teacher = "Петрова А.С.",
                            subject = "Программирование",
                            subgroup = "1 подгруппа",
                            group = "БПИ-228",
                            id = 2
                        )
                    )
                ),
                2 to DayUi(
                    date = "Вторник, 16 января",
                    lessons = emptyList()
                ),
                3 to DayUi(
                    date = "Среда, 17 января",
                    lessons = listOf(
                        LessonUi(
                            time = "13:00 - 14:30",
                            type = "Семинар",
                            location = "В-200",
                            teacher = "Сидоров П.В.",
                            subject = "История",
                            subgroup = "",
                            group = "БПИ-228",
                            id = 3
                        )
                    )
                )
            )
        ),
        WeekUi(
            type = "Нечётная неделя",
            days = mapOf(
                1 to DayUi(
                    date = "Понедельник, 22 января",
                    lessons = listOf(
                        LessonUi(
                            time = "11:00 - 12:30",
                            type = "Лабораторная",
                            location = "Л-404",
                            teacher = "Кузнецова М.И.",
                            subject = "Физика",
                            subgroup = "2 подгруппа",
                            group = "БПИ-228",
                            id = 4
                        )
                    )
                ),
                2 to DayUi(
                    date = "Вторник, 23 января",
                    lessons = listOf(
                        LessonUi(
                            time = "15:00 - 16:30",
                            type = "Практика",
                            location = "А-505",
                            teacher = "Николаев К.Р.",
                            subject = "Иностранный язык",
                            subgroup = "",
                            group = "БПИ-228",
                            id = 5
                        )
                    )
                ),
                3 to DayUi(
                    date = "Среда, 24 января",
                    lessons = emptyList()
                )
            )
        )
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimetableScreen()
        }
    }
}