package dev.keiji.sample.myapplication.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.keiji.sample.mastodonclient.Toot
import dev.keiji.sample.myapplication.MastodonApi
import dev.keiji.sample.myapplication.entity.UserCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TootRepository(
    private val userCredantial: UserCredential
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(userCredantial.instanceUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(MastodonApi::class.java)

    suspend fun fetchPublicTimeLine(
        maxId: String?,
        onlyMedia: Boolean
    ) = withContext(Dispatchers.IO) {
        api.fetchPublicTimeline(
            maxId = maxId,
            onlyMedia = onlyMedia
        )
    }

    suspend fun fetchHomeTimeline(
        maxId: String?
    ) = withContext(Dispatchers.IO) {
        api.fetchHomeTimeline(
            accessToken = "Bearer ${userCredantial.accessToken}",
            maxId = maxId
        )
    }

    suspend fun postToot(
        status: String
    ): Toot = withContext(Dispatchers.IO) {
        return@withContext api.postToot(
            "Bearer ${userCredantial.accessToken}",
            status
        )
    }

    suspend fun delete(id:String) = withContext(Dispatchers.IO) {
        api.deleteToot(
            "Bearer ${userCredantial.accessToken}",
            id
        )
    }
}
