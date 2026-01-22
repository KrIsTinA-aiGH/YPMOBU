package com.example.collegeschedule.data.dto

//dto для части занятия (для подгруппы)
data class LessonPartDto(
    val subject: String, //название предмета для подгруппы
    val teacher: String, //преподаватель для подгруппы
    val teacherPosition: String, //должность преподавателя
    val classroom: String, //аудитория для подгруппы
    val building: String, //корпус для подгруппы
    val address: String //адрес для подгруппы
)