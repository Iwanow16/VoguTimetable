package ru.vogu.timetable.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vogu.timetable.navigation.AppNavGraph
import ru.vogu.timetable.navigation.Screen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme /*VoguTimetableTheme*/ {

                val navController = rememberNavController()

                var topBarTitle by remember { mutableStateOf("") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        NavigationTopBar(topBarTitle, navController)
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        onSetTopBarTitle = { topBarTitle = it },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    title: String,
    navController: NavController
) {

    var canPop by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("") }

    navController.addOnDestinationChangedListener { controller, destination, _ ->
        canPop = controller.previousBackStackEntry != null
        currentRoute = destination.route ?: ""
    }

    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (canPop) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (navController.currentDestination?.route != Screen.Settings.route) {
                        navController.navigate(Screen.Settings.route)
                    }
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null
                    )
                }
            )
        }
    )
}