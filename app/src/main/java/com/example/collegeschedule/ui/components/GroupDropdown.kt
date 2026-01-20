package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.storage.FavoriteRepository

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun GroupDropdown(
    groups: List<GroupDto>,
    selectedGroup: GroupDto?,
    onGroupSelected: (GroupDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoriteRepository = remember { FavoriteRepository(context) }
    var expanded by remember { mutableStateOf(false) }

    // Состояние для принудительного обновления
    var refreshKey by remember { mutableStateOf(0) }

    // Обновляем состояние звезд при изменении избранного
    LaunchedEffect(refreshKey) {
        // Пустой эффект, просто для перерисовки
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = selectedGroup?.let { "${it.groupName} (${it.specialty})" } ?: "Выберите группу",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .weight(1f)
                    .padding(bottom = 8.dp)
            )

            // Кнопка избранного для выбранной группы
            selectedGroup?.let { group ->
                val isFavorite = favoriteRepository.isFavorite(group.groupId)
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            favoriteRepository.removeFromFavorites(group.groupId)
                        } else {
                            favoriteRepository.addToFavorites(group)
                        }
                        // Принудительно обновляем UI
                        refreshKey++
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = if (isFavorite) androidx.compose.ui.graphics.Color(0xFFFFD700) else androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (groups.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Нет доступных групп") },
                    onClick = {}
                )
            } else {
                groups.forEach { group ->
                    val isFavorite = favoriteRepository.isFavorite(group.groupId)

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = group.groupName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = group.specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }

                                // Кнопка избранного в выпадающем списке
                                IconButton(
                                    onClick = {
                                        // Останавливаем всплытие события к родителю
                                        if (isFavorite) {
                                            favoriteRepository.removeFromFavorites(group.groupId)
                                        } else {
                                            favoriteRepository.addToFavorites(group)
                                        }
                                        // Принудительно обновляем UI
                                        refreshKey++
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                                        tint = if (isFavorite) androidx.compose.ui.graphics.Color(0xFFFFD700) else androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            }
                        },
                        onClick = {
                            onGroupSelected(group)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}