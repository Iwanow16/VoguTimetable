package ru.test.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.entities.CabinetDb
import ru.test.data.storage.entities.DayDb
import ru.test.data.storage.entities.GroupDb
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.TeacherDb
import ru.test.data.storage.entities.TimetableDb
import ru.test.data.storage.entities.WeekDb

@Database(
    entities = [
        GroupDb::class,
        TeacherDb::class,
        CabinetDb::class,
        TimetableDb::class,
        WeekDb::class,
        DayDb::class,
        LessonDb::class
    ],
    exportSchema = false,
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun voguDao(): VoguDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}