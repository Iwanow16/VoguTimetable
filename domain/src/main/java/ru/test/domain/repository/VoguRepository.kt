package ru.test.domain.repository

import ru.test.domain.model.Group
import ru.test.domain.model.Week

interface VoguRepository {

    suspend fun parseData()

    suspend fun saveGroupId(groupId: Int)

    suspend fun clearGroupId()

    suspend fun getGroupsPaged(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<Group>

    suspend fun getTimetableForGroup(dateStart: String, dateEnd: String): List<Week>
}