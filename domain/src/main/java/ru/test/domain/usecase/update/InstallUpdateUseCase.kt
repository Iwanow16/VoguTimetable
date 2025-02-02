package ru.test.domain.usecase.update

import ru.test.domain.repository.UpdateRepository
import javax.inject.Inject

class InstallUpdateUseCase @Inject constructor(
    private val repository: UpdateRepository
) {
    suspend operator fun invoke(uri: String): Result<Unit> =
        repository.installUpdate(uri)
}