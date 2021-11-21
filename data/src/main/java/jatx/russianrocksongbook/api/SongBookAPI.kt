package jatx.russianrocksongbook.api

import io.reactivex.Single
import jatx.russianrocksongbook.api.gson.ResultWithAddSongListResultDataGson
import jatx.russianrocksongbook.api.gson.ResultWithCloudSongListDataGson
import jatx.russianrocksongbook.api.gson.ResultWithNumberGson
import jatx.russianrocksongbook.api.gson.ResultWithoutDataGson
import retrofit2.http.*

const val BASE_URL = "http://tabatsky.ru/SongBook/api/"

interface SongBookAPI {
    @FormUrlEncoded
    @POST("crashes/add")
    fun sendCrash(@FieldMap params: Map<String, String>): Single<ResultWithoutDataGson>

    @FormUrlEncoded
    @POST("songs/add")
    fun addSong(@FieldMap params: Map<String, String>): Single<ResultWithoutDataGson>

    @FormUrlEncoded
    @POST("songs/addList")
    fun addSongList(@FieldMap params: Map<String, String>): Single<ResultWithAddSongListResultDataGson>

    @FormUrlEncoded
    @POST("warnings/add")
    fun addWarning(@FieldMap params: Map<String, String>): Single<ResultWithoutDataGson>

    @GET("songs/search/{searchFor}/{orderBy}")
    fun searchSongs(
        @Path("searchFor") searchFor: String,
        @Path("orderBy") orderBy: String
    ): Single<ResultWithCloudSongListDataGson>

    @GET("songs/vote/{googleAccount}/{deviceIdHash}/{artist}/{title}/{variant}/{voteValue}")
    fun vote(
        @Path("googleAccount") googleAccount: String,
        @Path("deviceIdHash") deviceIdHash: String,
        @Path("artist") artist: String,
        @Path("title") title: String,
        @Path("variant") variant: Int,
        @Path("voteValue") voteValue: Int
    ): Single<ResultWithNumberGson>
    
    @GET("songs/getUploadsCountForUser/{googleAccount}/{deviceIdHash}")
    fun getUploadsCountForUser(
        @Path("googleAccount") googleAccount: String,
        @Path("deviceIdHash") deviceIdHash: String
    ): Single<ResultWithNumberGson>

    @GET("songs/delete/{secret1}/{secret2}/{artist}/{title}/{variant}")
    fun delete(
        @Path("secret1") secret1: String,
        @Path("secret2") secret2: String,
        @Path("artist") artist: String,
        @Path("title") title: String,
        @Path("variant") variant: Int,
    ): Single<ResultWithNumberGson>
}