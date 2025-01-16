package ru.test.data.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.test.data.storage.entities.CabinetDb.Companion.CABINETS_TABLE_NAME

@Entity(tableName = CABINETS_TABLE_NAME)
data class CabinetDb(
    @PrimaryKey val id: Int,
    val audience: String,
    val building: String,
    @SerializedName("build_location") val buildLocation: String
) {
    companion object {
        const val CABINETS_TABLE_NAME = "cabinets_table"
    }
}
