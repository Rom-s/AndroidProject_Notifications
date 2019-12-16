package com.example.td2

import com.squareup.moshi.Json

data class Task(
    @Json(name = "id")
    val id: String="",
    @Json(name = "title")
    val title: String="",
    @Json(name = "description")
    val description: String=""
)