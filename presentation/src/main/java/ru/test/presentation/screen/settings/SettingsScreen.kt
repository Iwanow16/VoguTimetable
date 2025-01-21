package ru.test.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.test.presentation.component.settings.SettingsSwitchItem
import ru.test.presentation.component.settings.SettingsTextItem

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onSetTopBarTitle: (String) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onSetTopBarTitle("Настройки")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SettingsSwitchItem(
            title = "Уведомления",
            description = "Включить push-уведомления",
            checked = remember { mutableStateOf(true) },
            onCheckedChange = { /* Обработка изменения */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsTextItem(
            title = "Аккаунт",
            description = "Управление данными профиля",
            onClick = { /* Обработка нажатия */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsTextItem(
            title = "Обновление приложения",
            description = "Текущая версия 1.0.0",
            onClick = {}
        )
    }
}