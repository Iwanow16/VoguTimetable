package ru.test.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.test.data.storage.entities.CabinetDb
import ru.test.data.storage.entities.DayDb
import ru.test.data.storage.entities.GroupDb
import ru.test.data.storage.entities.LessonDb
import ru.test.data.storage.entities.TeacherDb
import ru.test.data.storage.entities.TimetableDb
import ru.test.data.storage.entities.TimetableWithLessons
import ru.test.data.storage.entities.WeekDb

@Dao
interface VoguDao {

    // Timetable

    @Transaction
    @Query("SELECT * FROM ${TimetableDb.TIMETABLE_TABLE_NAME}")
    fun getTimetable(): TimetableWithLessons

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: TimetableDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeek(week: WeekDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: DayDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonDb)

    @Transaction
    suspend fun insertWeeksWithDaysAndLessons(
        timetableWithLessons: TimetableWithLessons
    ) {
        val timetable = timetableWithLessons.timetable
        val weeks = timetableWithLessons.weeks

        val timetableId = insertTimetable(timetable)

        weeks.forEach { week ->
            val weekId = insertWeek(week.week.copy(parentTimetableId = timetableId.toInt()))

            week.days.forEach { day ->
                val dayId = insertDay(day.day.copy(parentWeekId = weekId.toInt()))

                day.lessons.forEach { lesson ->
                    insertLesson(lesson.copy(parentDayId = dayId.toInt()))
                }
            }
        }
    }

    @Query("DELETE FROM ${TimetableDb.TIMETABLE_TABLE_NAME}")
    fun deleteAllTimetables()

    @Query("DELETE FROM ${WeekDb.WEEKS_TABLE_NAME}")
    fun deleteAllWeeks()

    @Query("DELETE FROM ${DayDb.DAYS_TABLE_NAME}")
    fun deleteAllDays()

    @Query("DELETE FROM ${LessonDb.LESSONS_TABLE_NAME}")
    fun deleteAllLessons()

    @Transaction
    fun clearTimetable() {
        deleteAllTimetables()
        deleteAllWeeks()
        deleteAllDays()
        deleteAllLessons()
    }

    // Groups

    @Query(
        """
        SELECT * FROM ${GroupDb.GROUPS_TABLE_NAME}
        WHERE name LIKE '%' || :query || '%'
        LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getParsedGroups(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<GroupDb>

    @Transaction
    fun updateGroups(groups: List<GroupDb>) {
        deleteAllGroups()
        insertGroups(groups)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroups(groups: List<GroupDb>)

    @Query("DELETE FROM ${GroupDb.GROUPS_TABLE_NAME}")
    fun deleteAllGroups()

    // Teachers

    @Query(
        """
        SELECT * FROM ${TeacherDb.TEACHERS_TABLE_NAME}
        WHERE name LIKE '%' || :query || '%'
        LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getParsedTeachers(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<TeacherDb>

    @Transaction
    fun updateTeachers(teachers: List<TeacherDb>) {
        deleteAllTeachers()
        insertTeachers(teachers)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeachers(teachers: List<TeacherDb>)

    @Query("DELETE FROM ${TeacherDb.TEACHERS_TABLE_NAME}")
    fun deleteAllTeachers()

    // Cabinets

    @Query(
        """
        SELECT * FROM ${CabinetDb.CABINETS_TABLE_NAME}
        WHERE buildLocation LIKE '%' || :query || '%'
        LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getParsedCabinets(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<CabinetDb>

    @Transaction
    fun updateCabinets(cabinets: List<CabinetDb>) {
        deleteAllCabinets()
        insertCabinets(cabinets)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCabinets(cabinets: List<CabinetDb>)

    @Query("DELETE FROM ${CabinetDb.CABINETS_TABLE_NAME}")
    fun deleteAllCabinets()
}