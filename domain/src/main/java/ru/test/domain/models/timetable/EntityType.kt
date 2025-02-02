package ru.test.domain.models.timetable

enum class EntityType(val type: String) {
    GROUP("group_id"),
    TEACHER("teacher_id"),
    CABINET("location_id")
}