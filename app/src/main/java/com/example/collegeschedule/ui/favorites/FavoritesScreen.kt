package com.example.collegeschedule.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.storage.FavoriteRepository

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onGroupSelected: (GroupDto) -> Unit = {}
) {
    val context = LocalContext.current
    val favoriteRepository = remember { FavoriteRepository(context) }

    var favorites by remember { mutableStateOf<List<GroupDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Загружаем избранные группы при открытии экрана
    LaunchedEffect(Unit) {
        favorites = favoriteRepository.getFavorites()
        loading = false
    }

    // Функция для обновления списка
    fun refreshFavorites() {
        favorites = favoriteRepository.getFavorites()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Загрузка...")
            }
        } else if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Нет избранных",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(text = "Нет избранных групп")
                    Text(
                        text = "Добавьте группы на главном экране",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Избранные группы",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Количество: ${favorites.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites) { group ->
                        FavoriteGroupCard(
                            group = group,
                            favoriteRepository = favoriteRepository,
                            onRemoved = { refreshFavorites() },
                            onClick = { onGroupSelected(group) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteGroupCard(
    group: GroupDto,
    favoriteRepository: FavoriteRepository,
    onRemoved: () -> Unit,
    onClick: (GroupDto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onClick(group) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = group.groupName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = group.specialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                    Text(
                        text = "Курс: ${group.course}",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }

                androidx.compose.material3.IconButton(
                    onClick = {
                        favoriteRepository.removeFromFavorites(group.groupId)
                        onRemoved()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Удалить из избранного"
                    )
                }
            }
        }
    }
}