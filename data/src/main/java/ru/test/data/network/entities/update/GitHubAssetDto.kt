package ru.test.data.network.entities.update

import com.google.gson.annotations.SerializedName

data class GitHubAssetDto(
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Long
)