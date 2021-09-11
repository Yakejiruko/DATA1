package dev.keiji.sample.myapplication

data class UserCredential (
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)
