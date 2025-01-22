package ru.test.data.network.services

import retrofit2.http.GET
import retrofit2.http.Path
import ru.test.data.network.entities.update.GitHubReleaseDto

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubReleaseDto
}