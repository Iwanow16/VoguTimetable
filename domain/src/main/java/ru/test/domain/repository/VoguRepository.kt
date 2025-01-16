package ru.test.domain.repository

import ru.test.domain.model.Group
import ru.test.domain.model.Week

interface VoguRepository {

    suspend fun parseData()

    suspend fun getGroupsFromCache(): List<Group>

    suspend fun getTimetableForGroup(groupId: Int): List<Week>
}