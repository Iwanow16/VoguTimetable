package ru.test.domain.model

data class Week(
    val days: Map<Int, Day>,
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