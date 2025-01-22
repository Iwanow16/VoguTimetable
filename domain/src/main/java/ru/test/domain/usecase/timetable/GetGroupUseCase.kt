package ru.test.domain.usecase.timetable

import ru.test.domain.models.timetable.EntityItem
import ru.test.domain.models.timetable.EntityType
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class GetEntityListByTypeUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(
        query: String,
        offset: Int,
        pageSize: Int,
        type: EntityType
    ): List<EntityItem> {


        return repository.getEntitiesByTypePaged(
            query = query,
            offset = offset,
            pageSize = pageSize,
            type = type
        )
    }
}