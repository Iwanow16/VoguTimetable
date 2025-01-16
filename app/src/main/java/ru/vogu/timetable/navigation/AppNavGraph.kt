package ru.vogu.timetable.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.test.presentation.screen.selection.SelectionScreen
import ru.test.presentation.screen.timetable.TimetableScreen

sealed class Screen(val route: String) {
    data object GroupSelection : Screen("group_selection")
    data object Timetable : Screen("timetable/{id}")
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.GroupSelection.route,
        modifier = modifier
    ) {
        composable(route = Screen.GroupSelection.route) {
            SelectionScreen(modifier) {
                println(it)
            }
        }

        composable(route = Screen.Timetable.route) {
            TimetableScreen(modifier) {
                println("BACK")
            }
        }
    }
}