package ru.test.data.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.test.data.storage.entities.GroupDb.Companion.GROUPS_TABLE_NAME

@Entity(tableName = GROUPS_TABLE_NAME)
data class GroupDb(
    @PrimaryKey val id: Int,
    val name: String,
    val course: String,
    @SerializedName("course_id") val courseId: String,
    val institute: String,
    @SerializedName("institute_id") val instituteId: String
) {
    companion object {
        const val GROUPS_TABLE_NAME = "groups_table"
    }
}
