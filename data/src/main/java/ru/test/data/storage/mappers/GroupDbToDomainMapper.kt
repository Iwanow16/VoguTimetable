package ru.test.data.storage.mappers

import ru.test.data.storage.entities.GroupDb
import ru.test.domain.model.Group
import javax.inject.Inject

class GroupDbToDomainMapper @Inject constructor() : (List<GroupDb>) -> List<Group> {

    override fun invoke(groups: List<GroupDb>): List<Group> =
        groups.map { groupDb ->
            Group(
                id = groupDb.id,
                name = groupDb.name
            )
        }
}