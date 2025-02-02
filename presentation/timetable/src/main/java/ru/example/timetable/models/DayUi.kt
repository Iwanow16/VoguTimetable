package ru.example.timetable.models

data class DayUi(
    val lessons: List<LessonUi>,
    val date: String
)