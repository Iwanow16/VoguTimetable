package ru.test.data.storage.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.test.data.storage.entities.DayDb.Companion.DAYS_TABLE_NAME
import ru.test.data.storage.entities.LessonDb.Companion.LESSONS_TABLE_NAME
import ru.test.data.storage.entities.TimetableDb.Companion.TIMETABLE_TABLE_NAME
import ru.test.data.storage.entities.WeekDb.Companion.WEEKS_TABLE_NAME

@Entity(tableName = TIMETABLE_TABLE_NAME)
data class TimetableDb(
    @PrimaryKey val timetableId: Int,
    val groupName: String
) {
    companion object {
        const val TIMETABLE_TABLE_NAME = "timetable_table"
    }
}

@Entity(tableName = WEEKS_TABLE_NAME)
data class WeekDb(
    @PrimaryKey(autoGenerate = true) val weekId: Int = 0,
    val parentTimetableId: Int,
    val type: String,
) {
    companion object {
        const val WEEKS_TABLE_NAME = "weeks_table"
    }
}

@Entity(tableName = DAYS_TABLE_NAME)
data class DayDb(
    @PrimaryKey(autoGenerate = true) val dayId: Int = 0,
    val parentWeekId: Int,
    val date: String,
) {
    companion object {
        const val DAYS_TABLE_NAME = "days_table"
    }
}

@Entity(tableName = LESSONS_TABLE_NAME)
data class LessonDb(
    @PrimaryKey val id: Int,
    val parentDayId: Int,
    val time: String,
    val type: String,
    val location: String,
    val teacher: String,
    val subject: String,
    val subgroup: String,
    val group: String
) {
    companion object {
        const val LESSONS_TABLE_NAME = "lessons_table"
    }
}

data class TimetableWitchWeeks(
    @Embedded val timetable: TimetableDb,
    @Relation(
        parentColumn = "timetableId",
        entityColumn = "parentTimetableId"
    )
    val weeks: List<WeekDb>
)

data class WeekWithDays(
    @Embedded val week: WeekDb,
    @Relation(
        parentColumn = "weekId",
        entityColumn = "parentWeekId"
    )
    val days: List<DayDb>
)

data class DayWithLessons(
    @Embedded val day: DayDb,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "parentDayId"
    )
    val lessons: List<LessonDb>
)

data class TimetableWithLessons(
    @Embedded val timetable: TimetableDb,
    @Relation(
        entity = WeekDb::class,
        parentColumn = "timetableId",
        entityColumn = "parentTimetableId"
    )
    val weeks: List<WeekWithDaysAndLessons>
)

data class WeekWithDaysAndLessons(
    @Embedded val week: WeekDb,
    @Relation(
        entity = DayDb::class,
        parentColumn = "weekId",
        entityColumn = "parentWeekId",
    )
    val days: List<DayWithLessons>
)