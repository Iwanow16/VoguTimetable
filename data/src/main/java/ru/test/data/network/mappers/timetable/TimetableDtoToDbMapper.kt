package ru.test.data.network.mappers.timetable

import ru.test.data.network.entities.timetable.DayDTO
import ru.test.data.network.entities.timetable.LessonDTO
import ru.test.data.network.entities.timetable.TimetableDTO
import ru.test.data.network.entities.timetable.WeekDTO
import ru.test.data.storage.entities.DayDb
import ru.test.data.storage.entities.DayWithLessons
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.TimetableDb
import ru.test.data.storage.entities.TimetableWithLessons
import ru.test.data.storage.entities.WeekDb
import ru.test.data.storage.entities.WeekWithDaysAndLessons
import javax.inject.Inject

class TimetableDtoToDbMapper @Inject constructor() : (TimetableDTO, Int) -> TimetableWithLessons {

    override fun invoke(
        timetable: TimetableDTO,
        timetableId: Int
    ): TimetableWithLessons =
        TimetableWithLessons(
            timetable = timetable.timetableDtoToDb().copy(timetableId = timetableId),
            weeks = timetable.schedule.map { week ->

                WeekWithDaysAndLessons(
                    week = week.weekDtoToDb(),
                    days = week.days.map { dayMap ->

                        val day = dayMap.value

                        DayWithLessons(
                            day = day.dayDtoToDb(),
                            lessons = day.lessons.map { lesson ->
                                lesson.lessonDtoToDb()
                            }
                        )
                    }
                )
            }
        )

    private fun TimetableDTO.timetableDtoToDb() =
        TimetableDb(
            timetableId = 0,
            groupName = this.title
        )

    private fun WeekDTO.weekDtoToDb(): WeekDb =
        WeekDb(
            parentTimetableId = 0, // Измениться при insert
            type = this.type,
        )

    private fun DayDTO.dayDtoToDb(): DayDb =
        DayDb(
            parentWeekId = 0, // Измениться при insert
            date = this.date
        )

    private fun LessonDTO.lessonDtoToDb(): LessonDb =
        LessonDb(
            id = this.id,
            parentDayId = 0, // Измениться при insert
            time = this.time,
            type = this.type,
            location = this.location,
            teacher = this.teacher,
            subject = this.subject,
            subgroup = this.subgroup,
            group = this.group
        )
}