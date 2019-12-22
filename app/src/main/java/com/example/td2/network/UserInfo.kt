package com.example.td2.network

import com.squareup.moshi.Json

data class UserInfo(
    @Json(name = "email")
    val email: String,
    @Json(name = "firstname")
    val firstname: String,
    @Json(name = "lastname")
    val lastname: String,
    @Json(name = "avatar")
    val avatar: String?
)