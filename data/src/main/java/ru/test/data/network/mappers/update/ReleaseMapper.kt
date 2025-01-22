package ru.test.data.network.mappers.update

import ru.test.data.network.entities.update.GitHubReleaseDto
import ru.test.domain.models.update.Release
import javax.inject.Inject

class ReleaseMapper @Inject constructor() {
    fun mapToDomain(dto: GitHubReleaseDto): Release {
        val apkAsset = dto.assets.first { it.name.endsWith(".apk") }
        return Release(
            version = dto.tagName.removePrefix("v"),
            changelog = dto.changelog,
            downloadUrl = apkAsset.downloadUrl,
            fileSize = apkAsset.size
        )
    }
}