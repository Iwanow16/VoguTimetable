package ru.test.data.storage.mappers

import ru.test.data.storage.entities.WeekWithDaysAndLessons
import ru.test.domain.model.Day
import ru.test.domain.model.Lesson
import ru.test.domain.model.Week
import javax.inject.Inject

class WeekDbToDomainMapper @Inject constructor() : (List<Int>) -> List<Week> {

    override fun invoke(weeks: List<Int>): List<Week> {
        return listOf()
    }
}