package ru.test.data.network.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import ru.test.data.network.entities.DayDTO
import ru.test.data.network.entities.LessonDTO
import ru.test.data.network.entities.TimetableDTO
import ru.test.data.network.entities.WeekDTO

class TimetableTypeAdapter : TypeAdapter<TimetableDTO>() {
    override fun write(out: JsonWriter, value: TimetableDTO?) {
        throw UnsupportedOperationException("Writing is not supported")
    }

    override fun read(reader: JsonReader): TimetableDTO {
        reader.beginObject()
        var title = ""
        var searchFor = ""
        var lastUpdate = ""
        var examinationSession: String? = null
        var pedagogicalPractice: String? = null
        var schedule: List<WeekDTO> = emptyList()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "title" -> title = reader.nextString()
                "search_for" -> searchFor = reader.nextString()
                "last_update" -> lastUpdate = reader.nextString()
                "examination_session" -> examinationSession = readNullableString(reader)
                "pedagogical_practice" -> pedagogicalPractice = readNullableString(reader)
                "schedule" -> schedule = readSchedule(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return TimetableDTO(
            title = title,
            searchFor = searchFor,
            schedule = schedule,
            lastUpdate = lastUpdate,
            examinationSession = examinationSession,
            pedagogicalPractice = pedagogicalPractice
        )
    }

    private fun readNullableString(reader: JsonReader): String? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            reader.nextString()
        }
    }

    private fun readSchedule(reader: JsonReader): List<WeekDTO> {
        val result = mutableListOf<WeekDTO>()
        reader.beginArray()

        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                // Читаем неделю
                val weekData = readWeek(reader)
                result.add(weekData)
            } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                // Читаем массив с типами недель
                val weekTypes = readWeekTypes(reader)
                // Присваиваем типы неделям
                result.forEachIndexed { index, week ->
                    result[index] = week.copy(type = weekTypes[index])
                }
                break
            }
        }

        reader.endArray()
        return result
    }

    private fun readWeek(reader: JsonReader): WeekDTO {
        val days = mutableMapOf<String, DayDTO>()
        reader.beginObject()

        while (reader.hasNext()) {
            val dayNumber = reader.nextName()
            days[dayNumber] = readDay(reader)
        }

        reader.endObject()
        return WeekDTO(weeks = days, type = "") // Тип недели будет установлен позже
    }

    private fun readDay(reader: JsonReader): DayDTO {
        reader.beginArray()

        val lessons = mutableListOf<LessonDTO>()

        reader.beginArray()
        if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            while (reader.hasNext()) {
                val lesson = readLesson(reader)
                lessons.add(lesson)
            }
        }
        reader.endArray()

        val date = reader.nextString()

        reader.endArray()
        return DayDTO(lessons = lessons, date = date)
    }

    private fun readLesson(reader: JsonReader): LessonDTO {
        reader.beginArray()

        val time = reader.nextString()
        val type = reader.nextString()
        val location = reader.nextString()
        val teacher = reader.nextString()
        val subject = reader.nextString()
        val subgroup = reader.nextString()
        val group = reader.nextString()
        val id = reader.nextInt()

        reader.endArray()
        return LessonDTO(
            time = time,
            type = type,
            location = location,
            teacher = teacher,
            subject = subject,
            subgroup = subgroup,
            group = group,
            id = id
        )
    }

    private fun readWeekTypes(reader: JsonReader): List<String> {
        val types = mutableListOf<String>()
        reader.beginArray()
        while (reader.hasNext()) {
            types.add(reader.nextString())
        }
        reader.endArray()
        return types
    }
}