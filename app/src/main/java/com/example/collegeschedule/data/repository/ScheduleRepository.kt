package com.example.collegeschedule.data.repository

import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.dto.ScheduleByDateDto

//репозиторий для работы с расписанием
class ScheduleRepository(private val api: ScheduleApi) {
    //загрузка расписания для группы с фиксированными датами
    suspend fun loadSchedule(group: String): List<ScheduleByDateDto> {
        return api.getSchedule(
            groupName = group,
            start = "2026-01-12", //фиксированная начальная дата
            end = "2026-01-17" //фиксированная конечная дата
        )
    }
}