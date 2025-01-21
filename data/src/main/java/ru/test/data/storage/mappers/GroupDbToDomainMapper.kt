package ru.test.data.storage.mappers

import ru.test.data.storage.entities.GroupDb
import ru.test.domain.models.EntityItem
import javax.inject.Inject

class GroupDbToDomainMapper @Inject constructor() : (List<GroupDb>) -> List<EntityItem> {

    override fun invoke(groups: List<GroupDb>): List<EntityItem> =
        groups.map { groupDb ->
            EntityItem(
                id = groupDb.id,
                name = groupDb.name
            )
        }
}