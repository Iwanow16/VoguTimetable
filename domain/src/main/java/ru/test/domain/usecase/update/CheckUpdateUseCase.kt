package ru.test.domain.usecase.update

import ru.test.domain.models.update.UpdateStatus
import ru.test.domain.repository.UpdateRepository
import javax.inject.Inject

class CheckUpdateUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(): Result<UpdateStatus> {
        return try {
            val currentVersion = updateRepository.getCurrentVersion()
            val latestRelease = updateRepository.getLatestRelease().getOrThrow()

            val updateAvailable = isNewerVersion(currentVersion, latestRelease.version)

            Result.success(
                UpdateStatus(
                    isAvailable = updateAvailable,
                    release = if (updateAvailable) latestRelease else null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toInt() }
        val latestParts = latest.removePrefix("v").split(".").map { it.toInt() }

        return latestParts.zip(currentParts)
            .find { it.first != it.second }
            ?.let { it.first > it.second }
            ?: (latestParts.size > currentParts.size)
    }
}