package ru.test.domain.usecase.timetable

import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class ParseDataUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke() = repository.parseData()
}