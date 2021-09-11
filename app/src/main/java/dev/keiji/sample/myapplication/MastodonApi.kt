package dev.keiji.sample.myapplication

import dev.keiji.sample.mastodonclient.Toot
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MastodonApi {

    @GET("api/v1/timelines/public")
    suspend fun  fetchPublicTimeline (
        @Query("max_id") maxId: String? = null,
        @Query("only_media") onlyMedia: Boolean = false
    ): List<Toot>

    @GET("api/vl/timelines/home")
    suspend fun fetchHomeTimeLine(
        @Header("Authorization") accessToken: String,
        @Query("max_id") maxId: String? = null
    ) : List<Toot>
}
