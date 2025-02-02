package ru.example.timetable.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun TimetableScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: TimetableScreenViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TimetableScreen(
        modifier = modifier,
        uiState = uiState,
        onScroll = { isPaging ->
            viewModel.postIntent(TimetableScreenIntent.LoadMore(isPaging))
        },
    )
}