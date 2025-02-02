package ru.test.domain.usecase.update

import ru.test.domain.repository.UpdateRepository
import javax.inject.Inject

class GetCurrentVersionUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(): String {
        return updateRepository.getCurrentVersion()
    }
}