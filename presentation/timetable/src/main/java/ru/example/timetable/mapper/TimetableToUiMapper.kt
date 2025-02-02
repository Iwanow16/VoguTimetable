package ru.example.timetable.mapper

import ru.example.timetable.models.DayUi
import ru.example.timetable.models.LessonUi
import ru.example.timetable.models.TimetableUi
import ru.example.timetable.models.WeekUi
import ru.test.domain.models.timetable.Day
import ru.test.domain.models.timetable.Lesson
import ru.test.domain.models.timetable.Timetable
import ru.test.domain.models.timetable.Week
import javax.inject.Inject

class TimetableToUiMapper @Inject constructor() : (Timetable) -> TimetableUi {

    override fun invoke(timetable: Timetable): TimetableUi =
        TimetableUi(
            weeks = timetable.weeks.map { it.weekDomainToUi() },
            groupName = timetable.name,
            isOffline = timetable.isOffline
        )

    private fun Week.weekDomainToUi(): WeekUi =
        WeekUi(
            days = this.days.map { it.dayDomainToUi() },
            type = this.type
        )

    private fun Day.dayDomainToUi(): DayUi =
        DayUi(
            lessons = this.lessons.map { it.lessonDomainToUi() },
            date = this.date
        )

    private fun Lesson.lessonDomainToUi(): LessonUi =
        LessonUi(
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