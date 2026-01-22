package com.example.collegeschedule.data.dto

//dto для расписания на конкретную дату
data class ScheduleByDateDto(
    val lessonDate: String, //дата занятий в формате iso
    val weekday: String, //день недели
    val lessons: List<LessonDto> //список занятий на эту дату
)