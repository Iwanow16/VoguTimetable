package ru.test.domain.usecase

import ru.test.domain.model.Group
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class GetGroupUseCase @Inject constructor(
    private val repository: VoguRepository
) {

    suspend operator fun invoke(): List<Group> {
        return repository.getGroupsFromCache()
    }
}