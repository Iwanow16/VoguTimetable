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
import ru.test.data.storage.entities.WeekDb
import ru.test.data.storage.entities.WeekWithDaysAndLessons

@Dao
interface VoguDao {

    // Timetable

    @Transaction
    @Query("SELECT * FROM ${WeekDb.WEEKS_TABLE_NAME}")
    fun getWeek(): List<WeekDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeks(vararg weeks: WeekDb): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(vararg days: DayDb): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(vararg lessons: LessonDb)

    @Query("DELETE FROM ${WeekDb.WEEKS_TABLE_NAME}")
    fun deleteAllWeeks()

    // Groups

    @Query("SELECT * FROM ${GroupDb.GROUPS_TABLE_NAME}")
    fun getAllGroups(): List<GroupDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroups(groups: List<GroupDb>)

    @Query("DELETE FROM ${GroupDb.GROUPS_TABLE_NAME}")
    fun deleteAllGroups()

    // Teachers

    @Query("SELECT * FROM ${TeacherDb.TEACHERS_TABLE_NAME}")
    fun getAllTeachers(): List<TeacherDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeachers(teachers: List<TeacherDb>)

    @Query("DELETE FROM ${TeacherDb.TEACHERS_TABLE_NAME}")
    fun deleteAllTeachers()

    // Cabinets

    @Query("SELECT * FROM ${CabinetDb.CABINETS_TABLE_NAME}")
    fun getAllCabinets(): List<CabinetDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCabinets(cabinets: List<CabinetDb>)

    @Query("DELETE FROM ${CabinetDb.CABINETS_TABLE_NAME}")
    fun deleteAllCabinets()
}