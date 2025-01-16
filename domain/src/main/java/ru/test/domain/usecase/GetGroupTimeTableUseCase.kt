package ru.test.domain.usecase

import ru.test.domain.model.Week
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class GetGroupTimeTableUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(): List<Week> =
        repository.getTimetableForGroup()
}