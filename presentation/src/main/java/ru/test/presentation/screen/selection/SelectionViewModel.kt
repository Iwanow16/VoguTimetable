package ru.test.presentation.screen.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private val _groups: MutableStateFlow<List<GroupUi>> = MutableStateFlow(emptyList())
    val groups: StateFlow<List<GroupUi>> = _groups.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLastPage = false

    init {
        observeQuery()
        loadNextPage()
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    private fun observeQuery() {
        viewModelScope.launch {
            _query.debounce(300)
                .distinctUntilChanged()
                .collect { newQuery ->
                    resetPagination()
                    loadNextPage(newQuery)
                }
        }
    }

    private fun resetPagination() {
        currentPage = 0
        isLastPage = false
        _groups.value = emptyList()
    }

    fun loadNextPage(query: String = _query.value) {
        if (isLastPage) return

        println(currentPage)

        viewModelScope.launch {
            val offset = currentPage * pageSize
            val newItems = getGroupUseCase(query, offset, pageSize)
                .map { groupToUiMapper.invoke(it) }

            if (newItems.isEmpty()) {
                isLastPage = true
            } else {
                _groups.value += newItems
                currentPage++
            }
        }
    }

    fun saveGroupId(groupId: Int) {
        viewModelScope.launch {
            saveGroupIdUseCase(groupId = groupId)
        }
    }
}