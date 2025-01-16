package ru.test.data.network.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import ru.test.data.network.entities.TimetableDTO

interface VoguService {

    @GET("/")
    suspend fun getToken()

    @POST("/")
    suspend fun getTimetable(
        @Body body: Map<String, String>,
//        @Header("X-Csrftoken") csrfToken: String
    ): TimetableDTO
}