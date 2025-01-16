package ru.test.presentation.screen.selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.test.presentation.component.SearchDropdownMenu

@Composable
fun SelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectionViewModel = hiltViewModel<SelectionViewModel>(),
    onTimetableClick: (groupId: Int) -> Unit = {},
) {

    val groups = viewModel.groups.collectAsState(initial = emptyList())

    Column(modifier = modifier.padding(16.dp)) {
        SearchDropdownMenu(
            label = "Группа",
            groups =groups.value,
        ) {
            onTimetableClick(it)
        }

        OutlinedButton(
            modifier = modifier
                .fillMaxWidth(),
            onClick = {

            }
        ) {
            Text("Перейти к расписанию")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectionScreen() {
    SelectionScreen()
}