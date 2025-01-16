package ru.test.data.network.services

import retrofit2.http.Body
import retrofit2.http.POST
import ru.test.data.network.entities.TimetableDTO

interface VoguService {

    @POST("/")
    suspend fun getTimetable(@Body body: Map<String, String>): TimetableDTO
}