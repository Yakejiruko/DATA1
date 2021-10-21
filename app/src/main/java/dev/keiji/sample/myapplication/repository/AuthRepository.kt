package dev.keiji.sample.myapplication.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.keiji.sample.myapplication.MastodonApi
import dev.keiji.sample.myapplication.entity.ResponceToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AuthRepository(
    instanceUrl: String
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(instanceUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(MastodonApi::class.java)

    suspend fun token(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        scopes: String,
        code: String
    ): ResponceToken = withContext(Dispatchers.IO) {
        return@withContext api.token(
            clientId,
            clientSecret,
            redirectUri,
            scopes,
            code,
            "authorization_code"
        )
    }
}