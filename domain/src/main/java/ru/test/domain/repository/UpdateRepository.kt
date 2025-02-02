package ru.test.domain.repository

import ru.test.domain.models.update.Release

interface UpdateRepository {
    suspend fun getLatestRelease(): Result<Release>
    suspend fun downloadUpdate(url: String): Result<String>
    suspend fun getCurrentVersion(): String
    suspend fun installUpdate(uri: String): Result<Unit>
    suspend fun cleanupUpdate(): Result<Unit>
}