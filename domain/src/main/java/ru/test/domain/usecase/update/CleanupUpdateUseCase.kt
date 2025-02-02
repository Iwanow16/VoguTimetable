package ru.test.domain.usecase.update

import ru.test.domain.repository.UpdateRepository
import javax.inject.Inject

class CleanupUpdateUseCase @Inject constructor(
    private val repository: UpdateRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.cleanupUpdate()
}