package ru.test.data.storage.mappers

import ru.test.data.storage.entities.DayWithLessons
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.TimetableWithLessons
import ru.test.data.storage.entities.WeekWithDaysAndLessons
import ru.test.domain.models.timetable.Day
import ru.test.domain.models.timetable.Lesson
import ru.test.domain.models.timetable.Timetable
import ru.test.domain.models.timetable.Week
import javax.inject.Inject

class WeekDbToDomainMapper @Inject constructor() : (TimetableWithLessons, Boolean) -> Timetable {

    override fun invoke(
        timetable: TimetableWithLessons,
        isOffline: Boolean
    ): Timetable =
        Timetable(
            name = timetable.timetable.groupName,
            weeks = timetable.weeks.map { it.weekDbToDomain() },
            isOffline = isOffline
        )

    private fun WeekWithDaysAndLessons.weekDbToDomain(): Week =
        Week(
            days = days.map { it.dayDbToDomain() },
            type = this.week.type
        )

    private fun DayWithLessons.dayDbToDomain(): Day =
        Day(
            lessons = lessons.map { it.lessonDbToDomain() },
            date = this.day.date
        )

    private fun LessonDb.lessonDbToDomain(): Lesson =
        Lesson(
            time = this.time,
            type = this.type,
            location = this.location,
            teacher = this.teacher,
            subject = this.subject,
            subgroup = this.subgroup,
            group = this.group,
            id = this.id
        )
}