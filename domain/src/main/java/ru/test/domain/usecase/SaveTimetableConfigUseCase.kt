package ru.test.domain.usecase

import ru.test.domain.models.EntityType
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class SaveTimetableConfigUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(
        timetableId: Int,
        entityType: EntityType
    ) {
        repository.saveTimetableConfig(timetableId, entityType)
    }
}