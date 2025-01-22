package ru.test.domain.usecase.update

import ru.test.domain.repository.UpdateRepository
import javax.inject.Inject

class DownloadUpdateUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(downloadUrl: String): Result<String> {
        return updateRepository.downloadUpdate(downloadUrl)
    }
}