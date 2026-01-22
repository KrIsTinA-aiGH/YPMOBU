package com.example.collegeschedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.storage.FavoriteRepository

@OptIn(ExperimentalMaterial3Api::class)
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
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) {
        // Пустой эффект для перерисовки
    }

    Column(modifier = modifier) {
        // Карточка выбранной группы
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка группы
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Группа",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация о группе
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (selectedGroup != null) {
                        Text(
                            text = selectedGroup.groupName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = selectedGroup.specialty,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "Выберите группу",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Кнопка избранного
                selectedGroup?.let { group ->
                    val isFavorite = favoriteRepository.isFavorite(group.groupId)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                if (isFavorite) {
                                    favoriteRepository.removeFromFavorites(group.groupId)
                                } else {
                                    favoriteRepository.addToFavorites(group)
                                }
                                refreshKey++
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            modifier = Modifier.size(20.dp),
                            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Стрелка раскрытия
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = if (expanded) "Свернуть список" else "Раскрыть список",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Выпадающий список
        if (expanded) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    if (groups.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Нет доступных групп",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Сначала избранные группы
                        val favoriteGroups = groups.filter { favoriteRepository.isFavorite(it.groupId) }
                        val otherGroups = groups.filterNot { favoriteRepository.isFavorite(it.groupId) }

                        if (favoriteGroups.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Избранное",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            favoriteGroups.forEach { group ->
                                GroupDropdownItem(
                                    group = group,
                                    isFavorite = true,
                                    favoriteRepository = favoriteRepository,
                                    onFavoriteToggle = { refreshKey++ },
                                    onClick = {
                                        onGroupSelected(group)
                                        expanded = false
                                    }
                                )
                            }

                            if (otherGroups.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Все группы",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        otherGroups.forEach { group ->
                            GroupDropdownItem(
                                group = group,
                                isFavorite = false,
                                favoriteRepository = favoriteRepository,
                                onFavoriteToggle = { refreshKey++ },
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
    }
}

@Composable
private fun GroupDropdownItem(
    group: GroupDto,
    isFavorite: Boolean,
    favoriteRepository: FavoriteRepository,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка группы
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFavorite) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = group.groupName.take(3),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (isFavorite) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация о группе
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = group.groupName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isFavorite) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${group.course} курс • ${group.specialty}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Кнопка избранного
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (isFavorite) {
                            favoriteRepository.removeFromFavorites(group.groupId)
                        } else {
                            favoriteRepository.addToFavorites(group)
                        }
                        onFavoriteToggle()
                    }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    modifier = Modifier.size(18.dp),
                    tint = if (isFavorite) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}