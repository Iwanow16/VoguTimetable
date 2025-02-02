package ru.example.timetable.models

data class TimetableUi(
    val weeks: List<WeekUi>,
    val groupName: String,
    val isOffline: Boolean
)