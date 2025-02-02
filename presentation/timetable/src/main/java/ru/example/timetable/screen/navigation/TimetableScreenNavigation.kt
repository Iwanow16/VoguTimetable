package ru.example.timetable.screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.example.timetable.screen.TimetableScreenRoute

const val TIMETABLE_SCREEN_ROUTE = "timetable_screen"
const val TIMETABLE_ID_ARG = "timetable_id"
const val TIMETABLE_TYPE_ARG = "timetable_type"

fun NavController.navigateToTimetable(
    timetableId: Int,
    timetableType: String
) {
    navigate(route = "$TIMETABLE_SCREEN_ROUTE/$timetableType/$timetableId") {
        launchSingleTop
    }
}

fun NavGraphBuilder.timetableScreen() {
    composable(
        route = "$TIMETABLE_SCREEN_ROUTE/{$TIMETABLE_TYPE_ARG}/{$TIMETABLE_ID_ARG}",
        arguments = listOf(
            navArgument(TIMETABLE_ID_ARG) { type = NavType.IntType },
            navArgument(TIMETABLE_TYPE_ARG) { type = NavType.StringType }
        )
    ) {
        TimetableScreenRoute()
    }
}