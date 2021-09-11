package dev.keiji.sample.myapplication

import android.app.Application
import android.net.wifi.hotspot2.pps.Credential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserCredentialRepository (
    private val application: Application
) {
    suspend fun find(
        instanceUrl: String,
        username: String
    ) : Credential.UserCredential? = withContext(Dispatchers.IO) {

        return@withContext Credential.UserCredential(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
            BuildConfig.ACCESS_TOKEN
        )
    }
}