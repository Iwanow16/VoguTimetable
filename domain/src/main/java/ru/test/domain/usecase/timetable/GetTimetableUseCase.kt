package ru.test.domain.usecase.timetable

import ru.test.domain.models.timetable.Timetable
import ru.test.domain.repository.VoguRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetTimetableUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(): Timetable {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dateStart = dateFormat.format(calendar.time)

        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 0)
        val dateEnd = dateFormat.format(calendar.time)

        return repository.getTimetableForGroup(dateStart, dateEnd)
    }
}