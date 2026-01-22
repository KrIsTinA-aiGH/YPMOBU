package com.example.collegeschedule.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//функция для получения диапазона дат на неделю
fun getWeekDateRange(): Pair<String, String> {
    val today = LocalDate.now() //сегодняшняя дата
    val formatter = DateTimeFormatter.ISO_DATE //форматер даты
    //если сегодня воскресенье — стартуем с понедельника
    var start = if (today.dayOfWeek == DayOfWeek.SUNDAY)
        today.plusDays(1)
    else
        today
    var daysAdded = 0
    var end = start
    //нужно получить ровно 6 учебных дней (пн–сб)
    while (daysAdded < 5) {
        end = end.plusDays(1)
        if (end.dayOfWeek != DayOfWeek.SUNDAY) {
            daysAdded++
        }
    }
    return start.format(formatter) to end.format(formatter) //возвращаем пару дат
}