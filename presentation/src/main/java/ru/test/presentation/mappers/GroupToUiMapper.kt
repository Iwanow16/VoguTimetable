package ru.test.presentation.mappers

import ru.test.domain.model.Group
import ru.test.presentation.models.GroupUi
import javax.inject.Inject

class GroupToUiMapper @Inject constructor() : (Group) -> GroupUi {

    override fun invoke(group: Group): GroupUi {
        return GroupUi(
            id = group.id,
            name = group.name
        )
    }
}