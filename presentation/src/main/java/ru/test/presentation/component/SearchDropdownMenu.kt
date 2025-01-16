package ru.test.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.test.presentation.models.GroupUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDropdownMenu(
    label: String,
    groups: List<GroupUi>,
    onGroupSelected: (Int) -> Unit
) {
    var text by remember { mutableStateOf("") }

    val filteredGroups = groups.filter {
        it.name.contains(text, ignoreCase = true)
    }

    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val isMenuExpanded = expanded && filteredGroups.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = isMenuExpanded,
        onExpandedChange = setExpanded,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                setExpanded(true)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable),
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { setExpanded(false) }
        ) {
            filteredGroups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        text = group.name
                        onGroupSelected(group.id)
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchDropDownMenu() {
    val sampleGroups = listOf(
        GroupUi(1, "Option 1"),
        GroupUi(2, "Яблоко"),
        GroupUi(3, "Груша")
    )

    SearchDropdownMenu(
        label = "TEST",
        groups = sampleGroups,
        onGroupSelected = {}
    )
}