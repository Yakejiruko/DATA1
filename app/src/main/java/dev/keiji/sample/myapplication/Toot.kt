package dev.keiji.sample.mastodonclient

import com.squareup.moshi.Json
import dev.keiji.sample.myapplication.Media

data class Toot (
    val id: String,
    @Json(name = "created_at") val createdAt: String,
    val sensitive: Boolean,
    val url: String,
    @Json(name = "media_attachments") val mediaAttachments: List<Media>,
    val content: String,
    val account: Account
) {
    val topMedia: Media?
        get() = mediaAttachments.firstOrNull()
}