package com.example.td2

import com.squareup.moshi.Json

data class TokenResponse(
    @Json(name = "token")
    val token : String
)

