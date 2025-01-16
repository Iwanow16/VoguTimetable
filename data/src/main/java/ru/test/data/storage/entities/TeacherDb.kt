package ru.test.data.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.test.data.storage.entities.TeacherDb.Companion.TEACHERS_TABLE_NAME

@Entity(tableName = TEACHERS_TABLE_NAME)
data class TeacherDb(
    @PrimaryKey val id: Int,
    @SerializedName("full_name") val name: String,
    @SerializedName("academic_degree") val academicDegree: String,
    @SerializedName("build_name") val buildName: String,
) {
    companion object {
        const val TEACHERS_TABLE_NAME = "teachers_table"
    }
}
