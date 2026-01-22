package com.example.collegeschedule.data.api

import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//интерфейс для api запросов к серверу расписания
interface ScheduleApi {
    //запрос расписания для группы за период
    @GET("api/schedule/group/{groupName}")
    suspend fun getSchedule(
        @Path("groupName") groupName: String, //название группы из пути url
        @Query("start") start: String, //начальная дата периода
        @Query("end") end: String //конечная дата периода
    ): List<ScheduleByDateDto>

    //запрос всех доступных групп
    @GET("api/Groups")
    suspend fun getAllGroups(): List<GroupDto>
}