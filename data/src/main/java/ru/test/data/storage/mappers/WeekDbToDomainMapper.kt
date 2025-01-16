package ru.test.data.storage.mappers

import ru.test.data.storage.entities.WeekWithDaysAndLessons
import ru.test.domain.model.Day
import ru.test.domain.model.Lesson
import ru.test.domain.model.Week
import javax.inject.Inject

class WeekDbToDomainMapper @Inject constructor() : (List<WeekWithDaysAndLessons>) -> List<Week> {

    override fun invoke(weeks: List<WeekWithDaysAndLessons>): List<Week> {
        return weeks.map { weekWithDaysAndLessons ->
            val week = weekWithDaysAndLessons.week
            val daysMap = weekWithDaysAndLessons.days.associateBy(
                { it.day.dayId },
                { dayWithLessons ->
                    Day(
                        lessons = dayWithLessons.lessons.map { lessonDb ->
                            Lesson(
                                time = lessonDb.time,
                                type = lessonDb.type,
                                location = lessonDb.location,
                                teacher = lessonDb.teacher,
                                subject = lessonDb.subject,
                                subgroup = lessonDb.subgroup,
                                group = lessonDb.group,
                                id = lessonDb.id
                            )
                        },
                        date = dayWithLessons.day.date
                    )
                }
            )

            Week(
                days = daysMap,
                type = week.type
            )
        }
    }
}