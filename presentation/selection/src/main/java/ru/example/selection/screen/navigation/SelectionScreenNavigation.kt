package ru.example.selection.screen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.example.selection.screen.SelectionScreenRoute

const val SELECTION_SCREEN_ROUTE = "selection_screen"

fun NavController.navigateToSelection() = navigate(SELECTION_SCREEN_ROUTE) {
    popUpTo(SELECTION_SCREEN_ROUTE) {
        inclusive = true
    }
}

fun NavGraphBuilder.selectionScreen(
    navigateToTimetable: (Int, String) -> Unit,
    setTopBarTitle: (String) -> Unit,
) {
    composable(route = SELECTION_SCREEN_ROUTE) {
        SelectionScreenRoute(
            navigateToTimetable = navigateToTimetable,
            onSetTopBarTitle = setTopBarTitle
        )
    }
}