package com.example.collegeschedule.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val groupId: Int,
    val groupName: String,
    val course: Int,
    val specialty: String
)