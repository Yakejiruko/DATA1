package dev.keiji.sample.myapplication.entity

import com.squareup.moshi.Json

data class ResponceToken(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    val scope: String,
    @Json(name="created_at") val createdAt: Long
) {
}