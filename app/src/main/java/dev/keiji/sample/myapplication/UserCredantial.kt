package dev.keiji.sample.myapplication

data class UserCredantial (
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)
