package com.example.jixiv.retrofit

import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.CoverShare
import com.example.jixiv.utils.ImageGroup
import com.example.jixiv.utils.PageResponse
import com.example.jixiv.utils.RegisterRequest
import com.example.jixiv.utils.Responses
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.ShareDetail
import com.example.jixiv.utils.User
import okhttp3.MultipartBody
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    data class UserUpdateRequest(
        val avatar: String?,
        val id: Long,
        val introduce: String?,
        val sex: Int?,
        val username: String?
    )
    @POST("user/update")
    fun userUpdate(
        @Body request: UserUpdateRequest
    ):Call<Responses<String>>

    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("username") username: String, @Field(
        "password") password: String):Call<Responses<String>>

    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") username: String, @Field(
        "password") password: String):Call<Responses<User>>

    @GET("user/getUserByName")
    fun getUserByName(
        @Query("username") username: String
    ): Call<Responses<User>>

    data class ShareSaveRequest(
        val content: String,
        val imageCode: Long,
        val pUserId: Long,
        val title: String
    )
    @POST("share/save")
    fun shareSave(
        @Body request: ShareSaveRequest
    ): Call<Responses<String>>

    @GET("share/save")
    fun shareGetSave(
        @Query("current")current:Int?,
        @Query("size")size:Int?,
        @Query("userId")userId:Long,
    ):Call<Responses<PageResponse<List<Share>>>>

    @GET("share/myself")
    fun getMyselfShare(
        @Query("current")current:Int?,
        @Query("size")size:Int?,
        @Query("userId")userId:Long,
    ):Call<Responses<PageResponse<List<Share>>>>

    @GET("share/detail")
    fun getShareDetail(
        @Query("shareId")shareId:Long,
        @Query("userId")userId:Long,
    ):Call<Responses<Share>>

    @FormUrlEncoded
    @POST("share/delete")
    fun deleteShare(
        @Field("shareId")shareId:Long,
        @Field("userId")userId:Long,
    ):Call<Responses<String>>

    data class ChangeShareRequest(
        val content: String,
        val id: Long,
        val imageCode: Long,
        val pUserId: Long,
        val title: String
    )
    @POST("share/change")
    fun changeShare(@Body request: ChangeShareRequest): Call<Responses<String>>

    data class ShareRequest(
        val content: String,
        val id: Long?,
        val imageCode: Long,
        val pUserId: Long,
        val title: String
    )
    @POST("share/add")
    fun addShare(@Body request: ShareRequest): Call<Responses<String>>

    @GET("share")
    fun getShareList(
        @Query("current")current:Int,
        @Query("size")size:Int,
        @Query("userId")userId:Long
    ):Call<Responses<PageResponse<List<Share>>>>

    @FormUrlEncoded
    @POST("like/cancel")
    fun likeCancel(
        @Field("likeId")likeId:Long
    ):Call<Responses<String>>

    @FormUrlEncoded
    @POST("like")
    fun like(
        @Field("shareId")shareId:Long,
        @Field("userId")userId:Long
    ):Call<Responses<String>>

    @GET("like")
    fun getLikeList(
        @Query("current")current:Int,
        @Query("size")size:Int,
        @Query("userId")userId:Long
    ):Call<Responses<PageResponse<List<Share>>>>

//    @Multipart
//    @POST("image/upload")
//    fun imageUpload(
//        @Part fileList: List<MultipartBody.Part>
//    ):Call<Responses<ImageGroup>>

    @Multipart
    @POST("image/upload")
    fun imageUpload(
        @Part fileList: List<MultipartBody.Part>
    ): Call<Responses<ImageGroup>>

    @FormUrlEncoded
    @POST("focus/cancel")
    fun focusCancel(
        @Field("focusUserId")focusUserId:Long,
        @Field("userId")userId:Long
    ):Call<Responses<String>>

    @FormUrlEncoded
    @POST("focus")
    fun focus(
        @Field("focusUserId")focusUserId:Long,
        @Field("userId")userId:Long
    ):Call<Responses<String>>

    @GET("focus")
    fun getFocusList(
        @Query("current")current:Int,
        @Query("size")size:Int,
        @Query("userId")userId:Long
    ):Call<Responses<PageResponse<List<Share>>>>

    data class SecondCommentRequest(
        val content: String,
        val parentCommentId: Long,
        val parentCommentUserId: Long,
        val replyCommentId: Long,
        val replyCommentUserId: Long,
        val shareId: Long,
        val userId: Long,
        val userName: String
    )
    @POST("comment/second")
    fun commentSecond(
        @Body request: SecondCommentRequest
    ): Call<Responses<String>>


    @GET("comment/second")
    fun getSecondComment(
        @Query("commentId")commentId:Long,
        @Query("current")current:Int?=null,
        @Query("shareId")shareId:Long,
        @Query("size")size:Int?=null,
    ):Call<Responses<PageResponse<List<Comment>>>>

    data class FirstCommentRequest(
        val content: String,
        val shareId: Long,
        val userId: Long,
        val userName: String
    )
    @POST("comment/first")
    fun commentFirst(
        @Body request: FirstCommentRequest
    ): Call<Responses<String>>

    @GET("comment/first")
    fun getFirstComment(
        @Query("current") current: Int?=null,
        @Query("shareId") shareId: Long,
        @Query("size") size: Int? = null
    ): Call<Responses<PageResponse<List<Comment>>>>

    @FormUrlEncoded
    @POST("collect/cancel")
    fun collectCancel(
        @Field("collectId")collectId:Long,
    ):Call<Responses<String>>

    @FormUrlEncoded
    @POST("collect")
    fun collect(
        @Field("shareId")shareId:Long,
        @Field("userId")userId:Long,
    ):Call<Responses<String>>

    @GET("collect")
    fun collect(
        @Query("current")current:Int,
        @Query("size")size:Int,
        @Query("userId")userId:Long
    ):Call<Responses<PageResponse<List<Share>>>>
}