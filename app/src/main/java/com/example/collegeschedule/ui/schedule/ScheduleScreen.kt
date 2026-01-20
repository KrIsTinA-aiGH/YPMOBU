package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.api.RetrofitInstance
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.ui.components.GroupDropdown
import com.example.collegeschedule.utils.getWeekDateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(modifier: Modifier = Modifier) {
    // Состояния
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var groups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<GroupDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Функция для загрузки расписания конкретной группы
    @Composable
    fun LoadScheduleForGroup(group: GroupDto?) {
        if (group == null) return

        LaunchedEffect(group.groupId) {
            loading = true
            error = null
            try {
                val (start, end) = getWeekDateRange()
                schedule = RetrofitInstance.api.getSchedule(
                    groupName = group.groupName,
                    start = start,
                    end = end
                )
            } catch (e: Exception) {
                error = "Ошибка загрузки расписания: ${e.message}"
                schedule = emptyList()
            } finally {
                loading = false
            }
        }
    }

    // Загрузка списка групп при первом запуске
    LaunchedEffect(Unit) {
        try {
            groups = RetrofitInstance.api.getAllGroups()

            // Автоматически выбираем первую группу
            if (groups.isNotEmpty()) {
                selectedGroup = groups[0]
            } else {
                error = "Нет доступных групп"
            }
            loading = false
        } catch (e: Exception) {
            error = "Ошибка загрузки групп: ${e.message}"
            loading = false
        }
    }

    // Загружаем расписание при изменении выбранной группы
    if (selectedGroup != null) {
        LoadScheduleForGroup(selectedGroup)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Выпадающий список для выбора группы
            GroupDropdown(
                groups = groups,
                selectedGroup = selectedGroup,
                onGroupSelected = { group ->
                    selectedGroup = group
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение состояния
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = error!!, color = Color.Red)
                    }
                }

                selectedGroup == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Выберите группу")
                    }
                }

                schedule.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Расписание для группы ${selectedGroup?.groupName ?: ""} не найдено")
                    }
                }

                else -> {
                    ScheduleList(
                        data = schedule,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}