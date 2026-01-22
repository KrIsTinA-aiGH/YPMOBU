package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme

//главная activity приложения
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //включаем edge-to-edge режим
        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp() //основное приложение
            }
        }
    }
}

//основное приложение с навигацией
@Composable
fun CollegeScheduleApp() {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) } //текущий экран
    //исправлено: правильный синтаксис remember
    var selectedGroupForSchedule by remember { mutableStateOf<GroupDto?>(null) } //выбранная группа для расписания

    Scaffold(
        bottomBar = {
            //нижняя навигационная панель
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination } //переключение экрана
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.HOME -> ScheduleScreen(
                modifier = Modifier.padding(innerPadding),
                preselectedGroup = selectedGroupForSchedule,
                onPreselectedGroupShown = { selectedGroupForSchedule = null }
            )

            AppDestinations.FAVORITES -> {
                //убран лишний text - оставляем только FavoritesScreen
                FavoritesScreen(
                    modifier = Modifier.padding(innerPadding),
                    onGroupSelected = { group ->
                        selectedGroupForSchedule = group
                        currentDestination = AppDestinations.HOME //переходим на главный экран
                    }
                )
            }

            AppDestinations.PROFILE -> Text(
                "Профиль студента",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

//перечисление экранов приложения
enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Главная", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Star),
    PROFILE("Профиль", Icons.Default.AccountBox),
}