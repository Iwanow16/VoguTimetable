package ru.test.data.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.database.AppDatabase
import ru.test.data.storage.database.AppDatabase.Companion.DATABASE_NAME
import ru.test.data.storage.database.DatabaseCallback
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ) =
        databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideVoguDao(db: AppDatabase): VoguDao = db.voguDao()
}