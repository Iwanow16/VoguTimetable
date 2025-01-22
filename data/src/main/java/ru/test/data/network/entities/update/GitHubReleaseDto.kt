package ru.test.data.network.entities.update

import com.google.gson.annotations.SerializedName

data class GitHubReleaseDto(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("body") val changelog: String,
    @SerializedName("assets") val assets: List<GitHubAssetDto>
)