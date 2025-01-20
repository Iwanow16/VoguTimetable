package ru.test.data.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.test.data.network.entities.TimetableDTO
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.TimetableTypeAdapter
import ru.test.data.storage.store.VoguStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private var csrfToken: String = ""

    @Provides
    fun provideCookieJar(store: VoguStore): CookieJar {
        return object : CookieJar {
            private val cookieStore = mutableMapOf<String, List<Cookie>>()

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
                cookies.find { it.name == "csrftoken" }?.let {
                    csrfToken = it.value
                }
            }
        }
    }

    @Provides
    fun provideOkHttpClient(
        cookieJar: CookieJar
    ): OkHttpClient =
        OkHttpClient.Builder()
            .cookieJar(cookieJar = cookieJar)
            .addInterceptor{ chain ->
                val original = chain.request()

                val modifiedRequest = if (csrfToken.isNotEmpty()) {
                    original.newBuilder()
                        .header("X-Csrftoken", csrfToken)
                        .build()
                } else {
                    original
                }

                chain.proceed(modifiedRequest)
            }
            .addNetworkInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                TimetableDTO::class.java,
                TimetableTypeAdapter()
            )
            .create()

        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
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