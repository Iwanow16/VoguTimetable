package ru.test.data.network.mappers.timetable

import ru.test.data.network.entities.timetable.DayDTO
import ru.test.data.network.entities.timetable.LessonDTO
import ru.test.data.network.entities.timetable.TimetableDTO
import ru.test.data.network.entities.timetable.WeekDTO
import ru.test.domain.models.timetable.Day
import ru.test.domain.models.timetable.Lesson
import ru.test.domain.models.timetable.Timetable
import ru.test.domain.models.timetable.Week
import javax.inject.Inject

class TimetableDtoToDomainMapper @Inject constructor() : (TimetableDTO) -> Timetable {

    override fun invoke(timetable: TimetableDTO): Timetable {
        return timetable.timetableDtoToDomain()
    }

    private fun TimetableDTO.timetableDtoToDomain(): Timetable =
        Timetable(
            name = this.title,
            weeks = this.schedule.map { it.weekDtoToDomain() },
            isOffline = false
        )

    private fun WeekDTO.weekDtoToDomain(): Week =
        Week(
            days = this.days.map { it.value.dayDtoToDomain() },
            type = this.type
        )

    private fun DayDTO.dayDtoToDomain(): Day =
        Day(
            lessons = this.lessons.map { it.lessonDtoToDomain() },
            date = this.date
        )

    private fun LessonDTO.lessonDtoToDomain(): Lesson =
        Lesson(
            id = this.id,
            time = this.time,
            type = this.type,
            location = this.location,
            teacher = this.teacher,
            subject = this.subject,
            subgroup = this.subgroup,
            group = this.group
        )
}