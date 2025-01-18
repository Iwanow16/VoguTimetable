package ru.test.domain.usecase

import ru.test.domain.model.Week
import ru.test.domain.repository.VoguRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetTimeTableUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(): List<Week> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dateStart = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 7)

        val dateEnd = dateFormat.format(calendar.time)

        return repository.getTimetableForGroup(dateStart, dateEnd)
    }
}