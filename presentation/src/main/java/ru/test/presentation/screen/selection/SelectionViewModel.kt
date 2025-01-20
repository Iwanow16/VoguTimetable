package ru.test.presentation.screen.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.test.domain.model.EntityType
import ru.test.domain.usecase.GetEntityListByTypeUseCase
import ru.test.domain.usecase.SaveTimetableConfigUseCase
import ru.test.presentation.mappers.EntityToUiMapper
import ru.test.presentation.models.MenuItem
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val getEntityListByTypeUseCase: GetEntityListByTypeUseCase,
    private val saveTimetableConfigUseCase: SaveTimetableConfigUseCase,
    private val groupToUiMapper: EntityToUiMapper,
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private val _type: MutableStateFlow<EntityType> = MutableStateFlow(EntityType.GROUP)

    private val _menuEntities: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val menuEntities: StateFlow<List<MenuItem>> = _menuEntities.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLastPage = false

    init {
        observeQuery()
        observeType()
        loadNextPage()
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setType(type: EntityType) {
        _type.value = type
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query.debounce(300)
                .distinctUntilChanged()
                .collect { newQuery ->
                    resetPagination()
                    loadNextPage(query = newQuery)
                }
        }
    }

    private fun observeType() {
        viewModelScope.launch {
            _type.collect { newType ->
                    resetPagination()
                    loadNextPage(type = newType)
                }
        }
    }

    private fun resetPagination() {
        currentPage = 0
        isLastPage = false
        _menuEntities.value = emptyList()
    }

    fun loadNextPage(
        query: String = _query.value,
        type: EntityType = _type.value
    ) {
        if (isLastPage) return

        viewModelScope.launch {
            val offset = currentPage * pageSize
            val newItems = getEntityListByTypeUseCase(query, offset, pageSize, type)
                .map { groupToUiMapper.invoke(it) }

            if (newItems.isEmpty()) {
                isLastPage = true
            } else {
                _menuEntities.value += newItems
                currentPage++
            }
        }
    }

    fun saveTimetableId(timetableId: Int) {
        viewModelScope.launch {
            saveTimetableConfigUseCase(
                timetableId = timetableId,
                entityType = _type.value
            )
        }
    }
}