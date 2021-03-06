package dev.keiji.sample.myapplication

import dev.keiji.sample.myapplication.entity.Account
import dev.keiji.sample.myapplication.entity.ResponceToken
import dev.keiji.sample.myapplication.entity.Toot
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApi {

    @GET("api/v1/timelines/public")
    suspend fun fetchPublicTimeline(
        @Query("max_id") maxId: String? = null,
        @Query("only_media") onlyMedia: Boolean = false
    ): List<Toot>

    @GET("api/v1/timelines/home")
    suspend fun fetchHomeTimeline(
        @Header("Authorization") accessToken: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int? = null
    ): List<Toot>

    @GET("api/v1/accounts/verify_credentials")
    suspend fun verifyAccountCredential(
        @Header("Authorization") accessToken: String
    ): Account

    @FormUrlEncoded
    @POST("api/v1/statuses")
    suspend fun postToot(
        @Header("Authorization") accessToken: String,
        @Field("status") status: String
    ): Toot

    @DELETE("api/v1/statuses/{id}")
    suspend fun deleteToot(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    )

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun  token(
        @Field("client_id") clientId:String,
        @Field("client_secret") clientSecret:String,
        @Field("redirect_uri") redirectUri:String,
        @Field("scope") scope:String,
        @Field("code") code:String,
        @Field("grant_type") grantType:String
    ) : ResponceToken
}
