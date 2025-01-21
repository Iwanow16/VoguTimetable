package ru.test.presentation.mappers

import ru.test.domain.models.EntityItem
import ru.test.presentation.models.MenuItem
import javax.inject.Inject

class EntityToUiMapper @Inject constructor() : (EntityItem) -> MenuItem {

    override fun invoke(group: EntityItem): MenuItem {
        return MenuItem(
            id = group.id,
            name = group.name
        )
    }
}