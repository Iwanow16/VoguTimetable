package ru.test.presentation.mappers

import ru.test.domain.models.timetable.Day
import ru.test.domain.models.timetable.Lesson
import ru.test.domain.models.timetable.Timetable
import ru.test.domain.models.timetable.Week
import ru.test.presentation.models.DayUi
import ru.test.presentation.models.LessonUi
import ru.test.presentation.models.TimetableUi
import ru.test.presentation.models.WeekUi
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