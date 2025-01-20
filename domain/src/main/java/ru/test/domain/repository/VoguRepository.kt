package ru.test.domain.repository

import ru.test.domain.model.EntityType
import ru.test.domain.model.EntityItem
import ru.test.domain.model.Week

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

    suspend fun getTimetableForGroup(dateStart: String, dateEnd: String): List<Week>
}