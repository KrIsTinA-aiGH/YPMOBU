package com.example.collegeschedule.storage

import android.content.Context
import com.example.collegeschedule.data.dto.GroupDto

class FavoriteRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    companion object {
        private const val FAVORITES_KEY = "favorite_groups"
        private const val SEPARATOR = "|||"
    }

    // Добавить группу в избранное
    fun addToFavorites(group: GroupDto) {
        val favorites = getFavoritesSet().toMutableSet()
        // Сохраняем все данные: id, название, специальность, курс
        favorites.add("${group.groupId}$SEPARATOR${group.groupName}$SEPARATOR${group.specialty}$SEPARATOR${group.course}")
        saveFavorites(favorites)
    }

    // Удалить группу из избранного
    fun removeFromFavorites(groupId: Int) {
        val favorites = getFavoritesSet().toMutableSet()
        favorites.removeAll { it.startsWith("$groupId$SEPARATOR") }
        saveFavorites(favorites)
    }

    // Получить все избранные группы
    fun getFavorites(): List<GroupDto> {
        val favoritesSet = getFavoritesSet()
        return favoritesSet.mapNotNull { serialized ->
            val parts = serialized.split(SEPARATOR)
            // Теперь ожидаем 4 части: id, название, специальность, курс
            if (parts.size == 4) {
                val groupId = parts[0].toIntOrNull()
                val groupName = parts[1]
                val specialty = parts[2]
                val course = parts[3].toIntOrNull() ?: 1
                if (groupId != null) {
                    GroupDto(groupId, groupName, course, specialty)
                } else {
                    null
                }
            } else if (parts.size == 2) {
                // Совместимость со старым форматом (только id и название)
                val groupId = parts[0].toIntOrNull()
                val groupName = parts[1]
                if (groupId != null) {
                    GroupDto(groupId, groupName, 1, "Не указано")
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    // Проверить, является ли группа избранной
    fun isFavorite(groupId: Int): Boolean {
        val favoritesSet = getFavoritesSet()
        return favoritesSet.any { it.startsWith("$groupId$SEPARATOR") }
    }

    // Получить Set строк (приватный метод)
    private fun getFavoritesSet(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }

    // Сохранить список избранных групп (приватный метод)
    private fun saveFavorites(favorites: Set<String>) {
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }
}