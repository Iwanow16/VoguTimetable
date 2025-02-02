package ru.example.selection.mapper

import ru.example.selection.models.MenuItem
import ru.test.domain.models.timetable.EntityItem
import javax.inject.Inject

class EntityToUiMapper @Inject constructor() : (EntityItem) -> MenuItem {

    override fun invoke(group: EntityItem): MenuItem {
        return MenuItem(
            id = group.id,
            name = group.name
        )
    }
}