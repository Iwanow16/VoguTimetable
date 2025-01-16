package ru.test.data.network.mappers

import ru.test.data.network.entities.WeekDTO
import ru.test.data.storage.entities.DayDb
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.WeekDb
import javax.inject.Inject

typealias InsertWeekModel = List<Pair<WeekDb, List<Pair<DayDb, List<LessonDb>>>>>

class WeekDtoToDbMapper @Inject constructor() : (List<WeekDTO>) -> InsertWeekModel {

    override fun invoke(weeks: List<WeekDTO>): InsertWeekModel {
        return weeks.map { weekDTO ->
            val weekDb = WeekDb(type = weekDTO.type)

            val daysWithLessons = weekDTO.weeks.map { (_, dayDTO) ->

                val dayDb = DayDb(date = dayDTO.date, parentWeekId = 0)

                val lessons = dayDTO.lessons.map { lessonDTO ->
                    LessonDb(
                        id = lessonDTO.id,
                        parentDayId = 0,
                        time = lessonDTO.time,
                        type = lessonDTO.type,
                        location = lessonDTO.location,
                        teacher = lessonDTO.teacher,
                        subject = lessonDTO.subject,
                        subgroup = lessonDTO.subgroup,
                        group = lessonDTO.group
                    )
                }

                dayDb to lessons
            }

            weekDb to daysWithLessons
        }
    }
}