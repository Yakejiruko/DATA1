package dev.keiji.sample.myapplication.entity

data class UserCredential (
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)
