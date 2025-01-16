package ru.test.data.network.mappers

import ru.test.data.network.entities.WeekDTO
import ru.test.data.storage.entities.DayDb
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.WeekDb
import javax.inject.Inject

class WeekDtoToDbMapper @Inject constructor() : (WeekDTO) -> WeekDb {

    override fun invoke(weekDto: WeekDTO): WeekDb {
        return WeekDb(type = weekDto.type)
    }
}