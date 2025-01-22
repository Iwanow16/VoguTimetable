package ru.test.data.network.entities.timetable

import com.google.gson.annotations.SerializedName

data class TimetableDTO(
    @SerializedName("title") val title: String,
    @SerializedName("search_for") val searchFor: String,
    @SerializedName("schedule") val schedule: List<WeekDTO>,
    @SerializedName("last_update") val lastUpdate: String?,
    @SerializedName("examination_session") val examinationSession: String?,
    @SerializedName("pedagogical_practice") val pedagogicalPractice: String?,
)

data class WeekDTO(
    val days: Map<String, DayDTO>,
    val type: String
)

data class DayDTO(
    val lessons: List<LessonDTO>,
    val date: String
)

data class LessonDTO(
    val time: String,
    val type: String,
    val location: String,
    val teacher: String,
    val subject: String,
    val subgroup: String,
    val group: String,
    val id: Int
)