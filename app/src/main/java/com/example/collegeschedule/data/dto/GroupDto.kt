package com.example.collegeschedule.data.dto

import kotlinx.serialization.Serializable

//dto для учебной группы
@Serializable
data class GroupDto(
    val groupId: Int, //уникальный идентификатор группы
    val groupName: String, //название группы
    val course: Int, //курс обучения
    val specialty: String //специальность
)