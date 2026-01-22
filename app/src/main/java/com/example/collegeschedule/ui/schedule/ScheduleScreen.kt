package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeschedule.data.api.RetrofitInstance
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.ui.components.GroupDropdown
import com.example.collegeschedule.utils.getWeekDateRange

//главный экран с расписанием
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    preselectedGroup: GroupDto? = null, //предварительно выбранная группа
    onPreselectedGroupShown: () -> Unit = {} //колбэк когда группа показана
) {
    //состояния экрана
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) } //расписание
    var groups by remember { mutableStateOf<List<GroupDto>>(emptyList()) } //список групп
    var selectedGroup by remember { mutableStateOf<GroupDto?>(null) } //выбранная группа
    var loadingGroups by remember { mutableStateOf(true) } //загрузка групп
    var loadingSchedule by remember { mutableStateOf(false) } //загрузка расписания
    var error by remember { mutableStateOf<String?>(null) } //ошибка

    //обработка предварительно выбранной группы
    LaunchedEffect(preselectedGroup, groups) {
        if (preselectedGroup != null && groups.isNotEmpty() && selectedGroup?.groupId != preselectedGroup.groupId) {
            val fullGroup = groups.find { it.groupId == preselectedGroup.groupId }
            if (fullGroup != null) {
                selectedGroup = fullGroup
                onPreselectedGroupShown() //уведомляем что группа показана
            }
        }
    }

    //загрузка списка групп при первом запуске
    LaunchedEffect(Unit) {
        try {
            groups = RetrofitInstance.api.getAllGroups()
            loadingGroups = false
        } catch (e: Exception) {
            error = "Не удалось загрузить список групп"
            loadingGroups = false
        }
    }

    //загрузка расписания при изменении выбранной группы
    LaunchedEffect(selectedGroup?.groupId) {
        val group = selectedGroup ?: return@LaunchedEffect

        loadingSchedule = true
        error = null

        try {
            val (start, end) = getWeekDateRange()
            schedule = RetrofitInstance.api.getSchedule(
                groupName = group.groupName,
                start = start,
                end = end
            )
        } catch (e: Exception) {
            error = "Ошибка загрузки расписания"
            schedule = emptyList()
        } finally {
            loadingSchedule = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        //хедер с информацией о группе
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                //заголовок экрана
                Text(
                    text = "Расписание занятий",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                //выбор группы
                Text(
                    text = "Выберите группу:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                GroupDropdown(
                    groups = groups,
                    selectedGroup = selectedGroup,
                    onGroupSelected = { group ->
                        selectedGroup = group
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                //информация о выбранной группе
                selectedGroup?.let { group ->
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Специальность:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = group.specialty,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Курс:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${group.course} курс",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //основное содержимое
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when {
                loadingGroups -> {
                    //загрузка групп
                    LoadingState(
                        text = "Загружаем список групп...",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                error != null -> {
                    //ошибка
                    ErrorState(
                        message = error!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                selectedGroup == null -> {
                    //группа не выбрана
                    EmptyState(
                        title = "Выберите группу",
                        subtitle = "Расписание появится здесь",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                loadingSchedule -> {
                    //загрузка расписания
                    LoadingState(
                        text = "Загружаем расписание для ${selectedGroup?.groupName}...",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                schedule.isEmpty() -> {
                    //расписание пустое
                    EmptyState(
                        title = "Расписание не найдено",
                        subtitle = "Для группы ${selectedGroup?.groupName ?: ""} нет занятий на эту неделю",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    //отображение расписания
                    ScheduleList(
                        data = schedule,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

//состояние загрузки
@Composable
private fun LoadingState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

//состояние ошибки
@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ошибка",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

//пустое состояние
@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📅",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}