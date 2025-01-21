package ru.vogu.timetable.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.test.presentation.screen.selection.SelectionScreen
import ru.test.presentation.screen.settings.SettingsScreen
import ru.test.presentation.screen.timetable.TimetableScreen

sealed class Screen(val route: String) {
    data object GroupSelection : Screen("group_selection")
    data object Timetable : Screen("timetable")
    data object Settings : Screen("settings")
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onSetTopBarTitle: (String) -> Unit = {},
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.GroupSelection.route,
    ) {
        composable(route = Screen.GroupSelection.route) {
            SelectionScreen(
                onSetTopBarTitle = {
                    onSetTopBarTitle(it)
                },
                onTimetableClick = {
                    navController.navigate(Screen.Timetable.route)
                }
            )
        }

        composable(route = Screen.Timetable.route) {
            TimetableScreen(
                onSetTopBarTitle = {
                    onSetTopBarTitle(it)
                },
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onSetTopBarTitle = {
                    onSetTopBarTitle(it)
                }
            )
        }
    }
}