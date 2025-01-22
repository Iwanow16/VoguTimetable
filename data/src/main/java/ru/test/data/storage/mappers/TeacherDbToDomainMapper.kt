package ru.test.data.storage.mappers

import ru.test.data.storage.entities.TeacherDb
import ru.test.domain.models.timetable.EntityItem
import javax.inject.Inject

class TeacherDbToDomainMapper @Inject constructor() : (List<TeacherDb>) -> List<EntityItem> {

    override fun invoke(groups: List<TeacherDb>): List<EntityItem> =
        groups.map { groupDb ->
            EntityItem(
                id = groupDb.id,
                name = groupDb.name
            )
        }
}