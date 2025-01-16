package ru.test.data.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.test.data.network.entities.TimetableDTO
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.TimetableTypeAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {

        val gson = GsonBuilder()
            .registerTypeAdapter(
                TimetableDTO::class.java,
                TimetableTypeAdapter()
            )
            .create()

        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideVoguService(retrofit: Retrofit): VoguService {
        return retrofit.create(VoguService::class.java)
    }

    private val BASE_URL = "https://tt2.vogu35.ru/"
}