package dev.keiji.sample.myapplication

import android.net.UrlQuerySanitizer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TootRepository (
    private val userCredantial: UserCredential
){
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(userCredantial.instanceUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(MastodonApi::class.java)

    suspend fun fetchPubliTimeLine(
        maxId: String?,
        onlyMedia: Boolean
    ) = withContext(Dispatchers.IO) {
        api.fetchPublicTimeline(
            maxId = maxId,
            onlyMedia = onlyMedia
        )
    }

    suspend fun fetchHomeTimeline (
        maxId: String?
    ) = withContext(Dispatchers.IO) {
        api.fetchPublicTimeline(
            accessToken = "Bearer ${userCredantial.accessToken}" ,
            maxId = maxId
        )
    }
}