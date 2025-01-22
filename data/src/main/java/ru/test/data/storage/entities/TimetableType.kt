package ru.test.data.storage.entities

import ru.test.domain.models.timetable.EntityType

enum class TimetableType(val type: String) {
    GROUP("group_id"),
    TEACHER("teacher_id"),
    CABINET("location_id")
}

fun EntityType.domainToDto(): TimetableType {
    return when (this) {
        EntityType.TEACHER -> TimetableType.TEACHER
        EntityType.GROUP -> TimetableType.GROUP
        EntityType.CABINET -> TimetableType.CABINET
    }
}