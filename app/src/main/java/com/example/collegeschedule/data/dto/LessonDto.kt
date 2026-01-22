package com.example.collegeschedule.data.dto

//dto для занятия (пары)
data class LessonDto(
    val lessonNumber: Int, //номер пары (1, 2, 3...)
    val time: String, //время проведения
    val subject: String, //название предмета
    val teacher: String, //преподаватель
    val teacherPosition: String, //должность преподавателя
    val classroom: String, //номер аудитории
    val building: String, //корпус
    val address: String, //адрес
    val groupParts: Map<LessonGroupPart, LessonPartDto?> //распределение по подгруппам
)