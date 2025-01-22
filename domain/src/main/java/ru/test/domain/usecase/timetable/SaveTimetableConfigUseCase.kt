package ru.test.domain.usecase.timetable

import ru.test.domain.models.timetable.EntityType
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