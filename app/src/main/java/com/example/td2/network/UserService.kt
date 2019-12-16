package com.example.td2.network

import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("info")
    suspend fun getInfo(): Response<UserInfo>
}