package ru.test.presentation.mappers

import ru.test.domain.models.Day
import ru.test.domain.models.Lesson
import ru.test.domain.models.Week
import ru.test.presentation.models.DayUi
import ru.test.presentation.models.LessonUi
import ru.test.presentation.models.WeekUi
import javax.inject.Inject

class WeekToUiMapper @Inject constructor() : (Week) -> WeekUi {

    override fun invoke(week: Week): WeekUi {
        return WeekUi(
            days = mapDays(week.days),
            type = week.type
        )
    }

    private fun mapDays(days: Map<Int, Day>): Map<Int, DayUi> {
        return days.mapValues { (_, day) ->
            DayUi(
                lessons = mapLessons(day.lessons),
                date = day.date
            )
        }
    }

    private fun mapLessons(lessons: List<Lesson>): List<LessonUi> {
        return lessons.map { lesson ->
            LessonUi(
                time = lesson.time,
                type = lesson.type,
                location = lesson.location,
                teacher = lesson.teacher,
                subject = lesson.subject,
                subgroup = lesson.subgroup,
                group = lesson.group,
                id = lesson.id
            )
        }
    }
}