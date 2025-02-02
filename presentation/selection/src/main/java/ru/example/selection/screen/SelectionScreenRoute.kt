package ru.example.selection.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SelectionScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SelectionScreenViewModel = hiltViewModel(),
    navigateToTimetable: (Int, String) -> Unit,
    onSetTopBarTitle: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SelectionScreen(
        modifier = modifier,
        uiState = uiState,
        onTimetableClick = navigateToTimetable,
        onQueryChanged = { query, type ->
            viewModel.postIntent(SelectionScreenIntent.Search(query, type))
        },
        loadMore = {
            viewModel.postIntent(SelectionScreenIntent.LoadMore)
        },
        onSetTopBarTitle = onSetTopBarTitle
    )
}