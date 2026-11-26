package com.example.lifeboardapp.data.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifeboardapp.presentation.dashboard.DashboardScreen
import com.example.lifeboardapp.data.presentation.dashboard.LifeBoardViewModel
import com.example.lifeboardapp.presentation.habits.HabitScreen
import com.example.lifeboardapp.presentation.habits.HabitViewModel
import com.example.lifeboardapp.data.presentation.tasks.TaskViewModel
import com.example.lifeboardapp.data.presentation.tasks.TaskScreen
import com.example.lifeboardapp.data.presentation.checklists.ChecklistViewModel
import com.example.lifeboardapp.data.presentation.checklists.ChecklistScreen
import com.example.lifeboardapp.data.presentation.wishlist.WishlistViewModel
import com.example.lifeboardapp.presentation.wishlist.WishlistScreen
import com.example.lifeboardapp.data.presentation.bucketlist.BucketListViewModel
import com.example.lifeboardapp.data.presentation.journal.JournalViewModel
import com.example.lifeboardapp.data.presentation.journal.JournalScreen
import com.example.lifeboardapp.data.presentation.bucketlist.BucketListScreen

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: String,
    val selectedRoutes: List<String> = listOf(route)
)

private val bottomDestinations = listOf(
    BottomDestination("dashboard", "Home", "⌂"),
    BottomDestination("habits", "Habits", "✓"),
    BottomDestination("tasks", "Tasks", "☷"),
    BottomDestination("journal", "Journal", "▤"),
    BottomDestination(
        route = "more",
        label = "More",
        icon = "•••",
        selectedRoutes = listOf(
            "more",
            "checklists",
            "wishlist",
            "bucketlist"
        )
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LifeBoardNavigation() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                bottomDestinations.forEach { destination ->

                    NavigationBarItem(
                        selected = currentRoute in destination.selectedRoutes,

                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {
                            Text(destination.icon)
                        },

                        label = {
                            Text(destination.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("dashboard") {
                DashboardDestination(navController)
            }

            composable("habits") {

                val habitViewModel: HabitViewModel = viewModel()

                val habitState by habitViewModel.uiState
                    .collectAsStateWithLifecycle()

                HabitScreen(
                    state = habitState,

                    onAddHabit = { title, description, frequency, target ->
                        habitViewModel.addHabit(
                            title,
                            description,
                            frequency,
                            target
                        )
                    },

                    onUpdateHabit = { id, title, description, frequency, target ->
                        habitViewModel.updateHabit(
                            id,
                            title,
                            description,
                            frequency,
                            target
                        )
                    },

                    onDeleteHabit = { id ->
                        habitViewModel.deleteHabit(id)
                    },

                    onToggleCompletion = { id, completed ->
                        habitViewModel.toggleHabitCompletion(
                            id,
                            completed
                        )
                    }
                )
            }

            composable("tasks") {

                val taskViewModel: TaskViewModel = viewModel()

                val taskState by taskViewModel.uiState
                    .collectAsStateWithLifecycle()

                TaskScreen(
                    state = taskState,

                    onAddTask = { title, description, priority, dueDate ->
                        taskViewModel.addTask(
                            title,
                            description,
                            priority,
                            dueDate
                        )
                    },

                    onUpdateTask = { id, title, description, priority, dueDate ->
                        taskViewModel.updateTask(
                            id,
                            title,
                            description,
                            priority,
                            dueDate
                        )
                    },

                    onDeleteTask = { id ->
                        taskViewModel.deleteTask(id)
                    },

                    onToggleTask = { id, completed ->
                        taskViewModel.toggleTask(
                            id,
                            completed
                        )
                    }
                )
            }



            composable("journal") {
                val journalViewModel: JournalViewModel = viewModel()
                val uiState by journalViewModel.uiState.collectAsStateWithLifecycle()

                JournalScreen(
                    state = uiState,
                    onAddEntry = journalViewModel::addEntry,
                    onUpdateEntry = journalViewModel::updateEntry,
                    onDeleteEntry = journalViewModel::deleteEntry
                )
            }


            composable("more") {
                MoreScreen(navController)
            }


            composable("checklists") {

                val checklistViewModel: ChecklistViewModel = viewModel()

                val checklistState by checklistViewModel.uiState
                    .collectAsStateWithLifecycle()

                ChecklistScreen(
                    state = checklistState,

                    onSelectChecklist = { id ->
                        checklistViewModel.selectChecklist(id)
                    },

                    onAddChecklist = { title, description ->
                        checklistViewModel.addChecklist(title, description)
                    },

                    onUpdateChecklist = { id, title, description ->
                        checklistViewModel.updateChecklist(
                            id,
                            title,
                            description
                        )
                    },

                    onDeleteChecklist = { id ->
                        checklistViewModel.deleteChecklist(id)
                    },

                    onAddItem = { title ->
                        checklistViewModel.addItem(title)
                    },

                    onUpdateItem = { id, title ->
                        checklistViewModel.updateItem(id, title)
                    },

                    onToggleItem = { id, completed ->
                        checklistViewModel.toggleItem(id, completed)
                    },

                    onDeleteItem = { id ->
                        checklistViewModel.deleteItem(id)
                    }
                )
            }



            composable("wishlist") {
                val viewModel: WishlistViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                WishlistScreen(
                    state = uiState,
                    onAddItem = viewModel::addItem,
                    onUpdateItem = viewModel::updateItem,
                    onDeleteItem = viewModel::deleteItem,
                    onTogglePurchased = viewModel::togglePurchased
                )
            }


            composable("bucketlist") {
                val viewModel: BucketListViewModel = viewModel()

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                BucketListScreen(
                    state = uiState,
                    onAddItem = viewModel::addItem,
                    onUpdateItem = viewModel::updateItem,
                    onDeleteItem = viewModel::deleteItem,
                    onToggleCompleted = viewModel::toggleCompleted
                )
            }

        }
    }
}


@Composable
private fun DashboardDestination(
    navController: NavHostController
) {
    val dashboardViewModel: LifeBoardViewModel = viewModel()

    val uiState by dashboardViewModel.uiState
        .collectAsStateWithLifecycle()

    DashboardScreen(
        state = uiState,
        onNavigate = { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
        },
        onTaskToggle = { taskId, completed ->
            dashboardViewModel.updateTaskStatus(
                taskId = taskId,
                completed = completed
            )
        }
    )
}

@Composable
private fun MoreScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "More",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Explore other areas of your life.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MoreNavigationButton(
            title = "Checklists",
            onClick = { navController.navigate("checklists") }
        )

        MoreNavigationButton(
            title = "Wishlist",
            onClick = { navController.navigate("wishlist") }
        )

        MoreNavigationButton(
            title = "Bucket List",
            onClick = { navController.navigate("bucketlist") }
        )
    }
}

@Composable
private fun MoreNavigationButton(
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(title)
    }
}

@Composable
private fun PlaceholderScreen(
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "This section is coming soon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
