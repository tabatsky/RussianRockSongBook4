package jatx.russianrocksongbook.networking.songbookapi

import io.reactivex.Single
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithNumber
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithoutData
import jatx.russianrocksongbook.networking.gson.ResultWithCloudSongGsonListData
import retrofit2.http.*

const val BASE_URL = "http://tabatsky.ru/SongBook2/api/"

internal interface SongBookAPI {
    @FormUrlEncoded
    @POST("crashes/add")
    fun sendCrash(@FieldMap params: Map<String, String>): Single<ResultWithoutData>

    @FormUrlEncoded
    @POST("songs/add")
    fun addSong(@FieldMap params: Map<String, String>): Single<ResultWithoutData>

    @FormUrlEncoded
    @POST("songs/addList")
    fun addSongList(@FieldMap params: Map<String, String>): Single<ResultWithAddSongListResultData>

    @FormUrlEncoded
    @POST("warnings/add")
    fun addWarning(@FieldMap params: Map<String, String>): Single<ResultWithoutData>

    @GET("songs/search/{searchFor}/{orderBy}")
    fun searchSongs(
        @Path("searchFor") searchFor: String,
        @Path("orderBy") orderBy: String
    ): Single<ResultWithCloudSongGsonListData>

    @GET("songs/vote/{googleAccount}/{deviceIdHash}/{artist}/{title}/{variant}/{voteValue}")
    fun vote(
        @Path("googleAccount") googleAccount: String,
        @Path("deviceIdHash") deviceIdHash: String,
        @Path("artist") artist: String,
        @Path("title") title: String,
        @Path("variant") variant: Int,
        @Path("voteValue") voteValue: Int
    ): Single<ResultWithNumber>
    
    @GET("songs/getUploadsCountForUser/{googleAccount}/{deviceIdHash}")
    fun getUploadsCountForUser(
        @Path("googleAccount") googleAccount: String,
        @Path("deviceIdHash") deviceIdHash: String
    ): Single<ResultWithNumber>

    @GET("songs/delete/{secret1}/{secret2}/{artist}/{title}/{variant}")
    fun delete(
        @Path("secret1") secret1: String,
        @Path("secret2") secret2: String,
        @Path("artist") artist: String,
        @Path("title") title: String,
        @Path("variant") variant: Int,
    ): Single<ResultWithNumber>

    @GET("songs/pagedSearchWithLikes/{searchFor}/{orderBy}/{page}")
    fun pagedSearch(
        @Path("searchFor") searchFor: String,
        @Path("orderBy") orderBy: String,
        @Path("page") page: Int
    ): ResultWithCloudSongGsonListData
}