package com.example.collegeschedule.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//объект для работы с сетью, создает и хранит экземпляр retrofit
object RetrofitInstance {
    //создаем экземпляр retrofit с базовым url и конвертером gson
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5153/") //адрес локального сервера для эмулятора
        .addConverterFactory(GsonConverterFactory.create()) //конвертер json в объекты kotlin
        .build()

    //создаем api для работы с расписанием
    val api: ScheduleApi = retrofit.create(ScheduleApi::class.java)
}