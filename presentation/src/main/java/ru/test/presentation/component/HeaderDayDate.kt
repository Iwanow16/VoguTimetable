package ru.test.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeaderDayDate(
    date: String,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {

    val inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    val dayNameFormat = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    val dateMonthFormat = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())

    val localDate = LocalDate.parse(date, inputFormat)

    val dayName = dayNameFormat.format(localDate)
    val dateMonth = dateMonthFormat.format(localDate)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (isToday) {
            Text(
                text = "Сегодня",
                style = MaterialTheme.typography.bodySmall
                    .copy(color = MaterialTheme.colorScheme.primary),
                modifier = modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = dateMonth,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeaderDayDate() {
    HeaderDayDate("06.01.2025", isToday = true)
}