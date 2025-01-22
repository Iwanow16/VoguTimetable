package ru.test.presentation.screen.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.test.presentation.component.settings.SettingsTextItem

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    onSetTopBarTitle: (String) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onSetTopBarTitle("Настройки")
    }

    val updateState by viewModel.updateState.collectAsState()

    val context = LocalContext.current

    val version = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        
        when (val state = updateState) {
            UpdateState.Downloading -> {
                println("Downloading")
            }
            is UpdateState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            UpdateState.Initial -> {
                println("Initial")
            }
            UpdateState.Loading -> {
                println("Loading")
            }
            UpdateState.NoUpdate -> {
                Toast.makeText(context, "Нет обновлений", Toast.LENGTH_SHORT).show()
            }
            is UpdateState.Ready -> {
                println("Ready")
            }
            is UpdateState.UpdateAvailable -> {
                println("UpdateAvailable")
            }
        }

        SettingsTextItem(
            title = "Обновление приложения",
            description = "Текущая версия 1.0.0",
            onClick = {
                viewModel.checkForUpdates()
            }
        )
    }
}