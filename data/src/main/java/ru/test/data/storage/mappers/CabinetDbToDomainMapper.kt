package ru.test.data.storage.mappers

import ru.test.data.storage.entities.CabinetDb
import ru.test.domain.model.EntityItem
import javax.inject.Inject

class CabinetDbToDomainMapper @Inject constructor() : (List<CabinetDb>) -> List<EntityItem> {

    override fun invoke(groups: List<CabinetDb>): List<EntityItem> =
        groups.map { groupDb ->
            EntityItem(
                id = groupDb.id,
                name = groupDb.buildLocation
            )
        }
}