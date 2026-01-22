package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.LessonGroupPart
import com.example.collegeschedule.data.dto.ScheduleByDateDto

//список расписания на несколько дней
@Composable
fun ScheduleList(
    data: List<ScheduleByDateDto>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        //состояние пустого расписания
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Расписание на неделю отсутствует",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
        return
    }

    //ленивый столбец для дней недели
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp) //отступы между днями
    ) {
        items(data) { day ->
            DayScheduleSection(day = day) //секция на день
        }
    }
}

//секция расписания на один день
@Composable
private fun DayScheduleSection(day: ScheduleByDateDto) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        //заголовок дня
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //цветная полоска дня
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = day.weekday, //день недели
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = day.lessonDate, //дата
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (day.lessons.isEmpty()) {
            //нет занятий в этот день
            NoLessonsCard()
        } else {
            //список занятий на день
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                day.lessons.forEach { lesson ->
                    LessonCard(lesson = lesson) //карточка занятия
                }
            }
        }
    }
}

//карточка занятия (пары)
@Composable
private fun LessonCard(lesson: com.example.collegeschedule.data.dto.LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //верхняя строка: номер пары и время
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Пара ${lesson.lessonNumber}", //номер пары
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = lesson.time, //время пары
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //блоки занятий для каждой подгруппы
            val groupPartsEntries = lesson.groupParts.entries.toList()
            val hasValidParts = groupPartsEntries.any { it.value != null }

            if (!hasValidParts) {
                //если нет данных о подгруппах, показываем общую информацию
                Text(
                    text = lesson.subject, //предмет
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                LessonInfoRow(text = lesson.teacher) //преподаватель
                Spacer(modifier = Modifier.height(4.dp))
                LessonInfoRow(text = "${lesson.building}, ауд. ${lesson.classroom}") //аудитория
            } else {
                //показываем информацию по подгруппам
                groupPartsEntries.forEachIndexed { index, (part, partInfo) ->
                    if (partInfo != null) {
                        LessonPartItem(part = part, info = partInfo)

                        //разделитель между частями (кроме последней)
                        if (index < groupPartsEntries.size - 1 && groupPartsEntries[index + 1].value != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

//элемент занятия для подгруппы
@Composable
private fun LessonPartItem(
    part: LessonGroupPart, //часть группы (full, sub1, sub2)
    info: com.example.collegeschedule.data.dto.LessonPartDto
) {
    Column {
        //метка подгруппы
        val (partText, partColor) = when (part) {
            LessonGroupPart.FULL -> Pair(
                "Вся группа",
                MaterialTheme.colorScheme.primary
            )
            LessonGroupPart.SUB1 -> Pair(
                "Подгруппа 1",
                MaterialTheme.colorScheme.tertiary
            )
            LessonGroupPart.SUB2 -> Pair(
                "Подгруппа 2",
                MaterialTheme.colorScheme.secondary
            )
        }

        Text(
            text = partText,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = partColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        //предмет
        Text(
            text = info.subject,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        //преподаватель
        LessonInfoRow(text = info.teacher)

        Spacer(modifier = Modifier.height(4.dp))

        //аудитория
        LessonInfoRow(text = "${info.building}, ауд. ${info.classroom}")
    }
}

//строка с информацией (преподаватель, аудитория)
@Composable
private fun LessonInfoRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

//карточка "нет занятий"
@Composable
private fun NoLessonsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Занятий нет",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}