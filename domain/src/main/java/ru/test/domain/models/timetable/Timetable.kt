package ru.test.domain.models.timetable

data class Timetable(
    val name: String,
    val weeks: List<Week>,
    val isOffline: Boolean = true
)

data class Week(
    val days: List<Day>,
    val type: String
)

data class Day(
    val lessons: List<Lesson>,
    val date: String
)

data class Lesson(
    val time: String,
    val type: String,
    val location: String,
    val teacher: String,
    val subject: String,
    val subgroup: String,
    val group: String,
    val id: Int
)