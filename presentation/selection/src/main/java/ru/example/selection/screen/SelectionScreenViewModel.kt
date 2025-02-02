package ru.example.selection.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.example.mvi.MviIntent
import ru.example.mvi.MviState
import ru.example.mvi.MviViewModel
import ru.example.mvi.utils.LceState
import ru.example.selection.mapper.EntityToUiMapper
import ru.example.selection.models.MenuItem
import ru.test.domain.models.timetable.EntityType
import ru.test.domain.usecase.timetable.GetEntityListByTypeUseCase
import javax.inject.Inject

@HiltViewModel
internal class SelectionScreenViewModel @Inject constructor(
    private val getEntityListByTypeUseCase: GetEntityListByTypeUseCase,
    private val groupToUiMapper: EntityToUiMapper,
) : MviViewModel<SelectionScreenIntent, SelectionScreenState>() {

    override fun initialStateCreator(): SelectionScreenState =
        SelectionScreenState(LceState.Loading)

    private var currentQuery: String = ""
    private var currentType: EntityType = EntityType.GROUP

    private var currentPage: Int = 0
    private val pageSize: Int = 20

    private var isLastPage = false

    init {
        loadMore()
    }

    override fun handleIntent(intent: SelectionScreenIntent) {
        when (intent) {
            SelectionScreenIntent.LoadMore -> loadMore()
            is SelectionScreenIntent.Search -> search(intent.query, intent.type)
        }
    }

    private fun loadMore() {
        if (isLastPage) return

        viewModelScope.launch {
            val offset = currentPage * pageSize
            val newData = getEntityListByTypeUseCase(currentQuery, offset, pageSize, currentType)
                .map { groupToUiMapper.invoke(it) }
            val currentData = (uiState.value.screenData as? LceState.Content)?.content ?: emptyList()

            if (newData.size < pageSize) {
                isLastPage = true
            } else {
                updateState { it.copy(screenData = LceState.Content(currentData + newData)) }
                currentPage++
            }
        }
    }

    private fun search(query: String, type: EntityType) {
        viewModelScope.launch {

            isLastPage = false
            currentQuery = query
            currentType = type
            currentPage = 0

            val result = getEntityListByTypeUseCase(query, 0, pageSize, type)
                .map { groupToUiMapper.invoke(it) }

            if (result.size < pageSize) isLastPage = true

            updateState { it.copy(screenData = LceState.Content(result)) }
        }
    }
}

internal sealed interface SelectionScreenIntent : MviIntent {
    data class Search(val query: String, val type: EntityType) : SelectionScreenIntent
    data object LoadMore : SelectionScreenIntent
}

internal data class SelectionScreenState(
    val screenData: LceState<List<MenuItem>>
) : MviState