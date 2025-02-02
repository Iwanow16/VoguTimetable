package ru.example.selection.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.example.mvi.utils.LceState
import ru.example.selection.R
import ru.example.selection.components.PagedSearchDropdownMenu
import ru.example.selection.models.MenuItem
import ru.example.ui_kit.components.ErrorContent
import ru.example.ui_kit.ui.theme.VoguTimetableTheme
import ru.test.domain.models.timetable.EntityType

@Composable
internal fun SelectionScreen(
    modifier: Modifier = Modifier,
    uiState: SelectionScreenState,
    onSetTopBarTitle: (String) -> Unit,
    onTimetableClick: (Int, String) -> Unit,
    onQueryChanged: (String, EntityType) -> Unit,
    loadMore: () -> Unit
) {

    LaunchedEffect(Unit) {
        onSetTopBarTitle("Расписание")
    }

    when (val state = uiState.screenData) {

        is LceState.Content -> SelectionScreenContent(
            items = state.content,
            onTimetableClick = onTimetableClick,
            onQueryChanged = onQueryChanged,
            onLoadMore = loadMore
        )

        is LceState.Error -> ErrorContent(
            message = state.throwable.message.toString()
        )

        LceState.Loading -> {}
    }
}

@Composable
private fun SelectionScreenContent(
    modifier: Modifier = Modifier,
    items: List<MenuItem>,
    onTimetableClick: (Int, String) -> Unit,
    onQueryChanged: (String, EntityType) -> Unit,
    onLoadMore: () -> Unit,
) {

    val menuItemNames = stringArrayResource(R.array.titles_for_menu)
    val pagerState = rememberPagerState { menuItemNames.size }

    var selectedItemId by remember { mutableIntStateOf(-1) }
    val currentType = remember { mutableStateOf(EntityType.GROUP) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            menuItemNames.forEachIndexed { id, name ->
                TextButton(
                    content = {
                        Text(text = name, textAlign = TextAlign.Center)
                    },
                    onClick = {
                        currentType.value = EntityType.entries[id]
                        onQueryChanged("", currentType.value)
                        coroutineScope.launch { pagerState.scrollToPage(id) }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            PagedSearchDropdownMenu(
                modifier = modifier,
                label = menuItemNames[page],
                items = items,
                onSearchQueryChange = { query ->
                    println("$query ui")
                    onQueryChanged(query, currentType.value)
                },
                onItemSelected = { groupId ->
                    selectedItemId = groupId
                },
                loadMore = {
                    onLoadMore()
                }
            )
        }

        OutlinedButton(
            modifier = modifier.fillMaxWidth(),
            content = { Text(text = stringResource(R.string.btn_navigate_timetable)) },
            onClick = { onTimetableClick(selectedItemId, currentType.value.type) },
            enabled = selectedItemId != -1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectionScreenContentPreview() {
    val items = listOf(
        MenuItem(0, "0"),
        MenuItem(1, "2")
    )

    VoguTimetableTheme {
        SelectionScreenContent(
            items = items,
            onQueryChanged = { q, t -> },
            onTimetableClick = { id, type -> },
            onLoadMore = {}
        )
    }
}