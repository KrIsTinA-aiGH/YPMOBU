package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(data: List<ScheduleByDateDto>, modifier: Modifier) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(data) { day ->
            Text(
                text = "${day.lessonDate} (${day.weekday})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (day.lessons.isEmpty()) {
                Text(
                    text = "Информация отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            } else {
                day.lessons.forEach { lesson ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Пара ${lesson.lessonNumber} (${lesson.time})")
                            lesson.groupParts.forEach { (part, info) ->
                                if (info != null) {
                                    Text(text = "$part: ${info.subject}")
                                    Text(text = info.teacher)
                                    Text(text = "${info.building}, ${info.classroom}")
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}