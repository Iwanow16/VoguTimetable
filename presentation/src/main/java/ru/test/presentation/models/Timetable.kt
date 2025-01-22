package ru.test.presentation.models

data class TimetableUi(
    val weeks: List<WeekUi>,
    val groupName: String,
    val isOffline: Boolean
)

data class WeekUi(
    val days: List<DayUi>,
    val type: String
)

data class DayUi(
    val lessons: List<LessonUi>,
    val date: String
)

data class LessonUi(
    val time: String,
    val type: String,
    val location: String,
    val teacher: String,
    val subject: String,
    val subgroup: String,
    val group: String,
    val id: Int
)