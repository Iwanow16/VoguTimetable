package ru.test.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.test.data.network.mappers.update.ReleaseMapper
import ru.test.data.network.repository.UpdateRepositoryImpl
import ru.test.data.network.services.GitHubApiService
import ru.test.domain.repository.UpdateRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UpdateModule {

    @Provides
    @Named("repoOwner")
    fun provideRepoOwner(): String = "Iwanow16"

    @Provides
    @Named("repoName")
    fun provideRepoName(): String = "VoguTimetable"

    @Provides
    @Singleton
    fun provideGitHubApiService(): GitHubApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUpdateRepository(
        api: GitHubApiService,
        mapper: ReleaseMapper,
        @ApplicationContext context: Context,
        @Named("repoOwner") repoOwner: String,
        @Named("repoName") repoName: String
    ): UpdateRepository {
        return UpdateRepositoryImpl(api, mapper, context, repoOwner, repoName)
    }
}