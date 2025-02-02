package ru.test.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.test.data.repository.VoguRepositoryImpl
import ru.test.domain.repository.VoguRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindVoguRepository(
        impl: VoguRepositoryImpl
    ): VoguRepository
}