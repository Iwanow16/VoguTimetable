package ru.vogu.timetable.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.example.selection.screen.navigation.SELECTION_SCREEN_ROUTE
import ru.example.selection.screen.navigation.selectionScreen
import ru.example.settings.screen.navigation.settingsScreen
import ru.example.timetable.screen.navigation.navigateToTimetable
import ru.example.timetable.screen.navigation.timetableScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onSetTopBarTitle: (String) -> Unit = {},
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SELECTION_SCREEN_ROUTE,
    ) {

        selectionScreen(
            navigateToTimetable = { id, type ->
                navController.navigateToTimetable(id, type)
            },
            setTopBarTitle = onSetTopBarTitle
        )
        timetableScreen()

        settingsScreen()
    }
}