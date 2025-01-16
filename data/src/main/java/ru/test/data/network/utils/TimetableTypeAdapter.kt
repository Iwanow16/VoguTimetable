package ru.test.data.network.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import ru.test.data.network.entities.DayDTO
import ru.test.data.network.entities.LessonDTO
import ru.test.data.network.entities.WeekDTO

class TimetableTypeAdapter : TypeAdapter<List<WeekDTO>>() {
    override fun write(out: JsonWriter, value: List<WeekDTO>?) {
        out.beginArray()
        value?.forEach { week ->
            out.beginObject()
            week.weeks.forEach { (key, day) ->
                out.name(key)
                out.beginArray()
                // Записываем уроки
                out.beginArray()
                day.lessons.forEach { lesson ->
                    out.beginArray()
                    out.value(lesson.time)
                    out.value(lesson.type)
                    out.value(lesson.location)
                    out.value(lesson.teacher)
                    out.value(lesson.subject)
                    out.value(lesson.subgroup)
                    out.value(lesson.group)
                    out.value(lesson.id)
                    out.endArray()
                }
                out.endArray()
                // Записываем дату
                out.value(day.date)
                out.endArray()
            }
            out.endObject()
        }
        out.endArray()
    }

    override fun read(reader: JsonReader): List<WeekDTO> {
        val weeks = mutableListOf<WeekDTO>()
        reader.beginArray()

        while (reader.hasNext()) {
            when (reader.peek()) {
                JsonToken.BEGIN_OBJECT -> {
                    // Читаем объект недели
                    reader.beginObject()
                    val daysMap = mutableMapOf<String, DayDTO>()

                    while (reader.hasNext()) {
                        val dayNumber = reader.nextName()
                        reader.beginArray()

                        // Читаем массив уроков
                        reader.beginArray()
                        val lessons = mutableListOf<LessonDTO>()

                        while (reader.hasNext()) {
                            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                                reader.beginArray()
                                val lesson = LessonDTO(
                                    time = reader.nextString(),
                                    type = reader.nextString(),
                                    location = reader.nextString(),
                                    teacher = reader.nextString(),
                                    subject = reader.nextString(),
                                    subgroup = reader.nextString(),
                                    group = reader.nextString(),
                                    id = reader.nextInt()
                                )
                                reader.endArray()
                                lessons.add(lesson)
                            } else {
                                reader.skipValue()
                            }
                        }
                        reader.endArray()

                        // Читаем дату
                        val date = reader.nextString()
                        reader.endArray()

                        daysMap[dayNumber] = DayDTO(lessons, date)
                    }
                    reader.endObject()

                    weeks.add(WeekDTO(daysMap, ""))
                }

                JsonToken.BEGIN_ARRAY -> {
                    // Читаем массив с типами недель
                    reader.beginArray()
                    val weekTypes = mutableListOf<String>()
                    while (reader.hasNext()) {
                        weekTypes.add(reader.nextString())
                    }
                    reader.endArray()

                    // Обновляем типы недель в предыдущих объектах WeekDTO
                    if (weeks.size >= 2) {
                        weeks[0] = weeks[0].copy(type = weekTypes[0])
                        weeks[1] = weeks[1].copy(type = weekTypes[1])
                    }
                }

                else -> reader.skipValue()
            }
        }
        reader.endArray()

        return weeks
    }
}