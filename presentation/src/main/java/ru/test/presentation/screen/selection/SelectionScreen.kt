package ru.test.presentation.screen.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.test.domain.models.EntityType
import ru.test.presentation.component.selection.PagedSearchDropdownMenu
import ru.test.presentation.models.DropDownMenuInfo

@Composable
fun SelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectionViewModel = hiltViewModel<SelectionViewModel>(),
    onSetTopBarTitle: (String) -> Unit = {},
    onTimetableClick: (groupId: Int) -> Unit = {},
) {

    LaunchedEffect(Unit) {
        onSetTopBarTitle("Расписание")
    }

    val items by viewModel.menuEntities.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var selectedItemId by remember { mutableIntStateOf(-1) }
    val selectedItemType = remember {
        listOf(
            DropDownMenuInfo("Группа"),
            DropDownMenuInfo("Преподаватель"),
            DropDownMenuInfo("Кабинет")
        )
    }
    val selectedButton = remember {
        listOf(
            SelectionButton("Группа", EntityType.GROUP, 0),
            SelectionButton("Преподаватель", EntityType.TEACHER, 1),
            SelectionButton("Аудитория", EntityType.CABINET, 2)
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { selectedItemType.size },
    )

    Column(modifier = modifier.padding(16.dp)) {

        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(selectedButton) { button ->
                TextButton(
                    content = {
                        Text(
                            text = button.name,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        viewModel.setType(button.type)
                        coroutineScope.launch {
                            pagerState.scrollToPage(button.page)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            with(selectedItemType[page]) {
                PagedSearchDropdownMenu(
                    label = name,
                    items = items,
                    onItemSelected = {
                        selectedItemId = it
                        viewModel.saveTimetableId(it)
                    },
                    onSearchQueryChange = { viewModel.setQuery(it) },
                    onLoadNextPage = { viewModel.loadNextPage() }
                )
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            content = { Text("Перейти к расписанию") },
            onClick = { onTimetableClick(selectedItemId) },
            enabled = selectedItemId != -1
        )
    }
}

data class SelectionButton(
    val name: String,
    val type: EntityType,
    val page: Int
)

@Preview(showBackground = true)
@Composable
fun PreviewSelectionScreen() {
    SelectionScreen()
}