package com.example.td2.network

import com.example.td2.LoginForm
import com.example.td2.SignupForm
import com.example.td2.TokenResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body user: LoginForm): Response<TokenResponse>

    @POST("users/sign_up")
    suspend fun signup(@Body user: SignupForm): Response<TokenResponse>
}