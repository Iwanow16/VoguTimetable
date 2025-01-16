package ru.test.presentation.screen.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.test.domain.usecase.GetGroupUseCase
import ru.test.domain.usecase.ParseDataUseCase
import ru.test.domain.usecase.SaveGroupIdUseCase
import ru.test.presentation.mappers.GroupToUiMapper
import ru.test.presentation.models.GroupUi
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val parseDataUseCase: ParseDataUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val saveGroupIdUseCase: SaveGroupIdUseCase,
    private val groupToUiMapper: GroupToUiMapper,
) : ViewModel() {

    private val _groups: MutableStateFlow<List<GroupUi>> = MutableStateFlow(emptyList())
    val groups: StateFlow<List<GroupUi>> = _groups.asStateFlow()

    init {
        viewModelScope.launch {
            parseDataUseCase() // <--- Удали и сделай фоном, а первоначальное заполнение через функции Room
            _groups.value = getGroupUseCase()
                .map { groupToUiMapper.invoke(it) }
        }
    }

    fun saveGroupId(groupId: Int) {
        viewModelScope.launch {
            saveGroupIdUseCase(groupId = groupId)
        }
    }
}