package ru.test.domain.usecase.timetable

import ru.test.domain.models.timetable.Timetable
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class GetTimetableUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(
        timetableId: Int,
        timetableType: String,
        dateStart: String,
        dateEnd: String,
        isPaging: Boolean
    ): Result<Timetable> =
        repository.getTimetable(
            timetableId = timetableId,
            timetableType = timetableType,
            dateStart = dateStart,
            dateEnd = dateEnd,
            isPaging = isPaging
        )
}