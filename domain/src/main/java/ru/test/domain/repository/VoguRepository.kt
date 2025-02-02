package ru.test.domain.repository

import ru.test.domain.models.timetable.EntityItem
import ru.test.domain.models.timetable.EntityType
import ru.test.domain.models.timetable.Timetable

interface VoguRepository {

    suspend fun parseData()

    suspend fun saveTimetableConfig(
        timetableId: Int,
        type: EntityType
    )

    suspend fun clearGroupId()

    suspend fun getEntitiesByTypePaged(
        query: String,
        offset: Int,
        pageSize: Int,
        type: EntityType
    ): List<EntityItem>

    suspend fun getTimetable(
        timetableId: Int,
        timetableType: String,
        dateStart: String,
        dateEnd: String,
        isPaging: Boolean
    ): Result<Timetable>
}