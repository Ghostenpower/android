package com.example.jixiv.utils

data class Responses<T>(
    val code:Int,
    val msg:String,
    val data:T
)

data class User(
    val appKey:String,
    val id: Long,
    val username: String,
    val password: String?,
    val sex: Int?,
    val introduce: String?,
    val avatar: String?,
    val createTime: Long,
    val lastUpdateTime: Long
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class PageResponse<T>(
    val current:Int,
    val size:Int,
    val total:Int,
    val records: T
)

data class CoverShare(
     val shareId: Int,
     val author: String,
    val title: String,
     val content: String,
     val firstImageUrl: String,
     val imgNum: Int,
     val likeNum: Int,
     val collectNum: Int,
     val isFavorited: Boolean,
     val isCollected: Boolean
)

data class ShareDetail (
     val shareId: Int,
     val author: String,
     val authorId: String,
     val authorAvatar: String,
     val title: String,
     val content: String,
     val imageUrlList: List<String>,
     val imgNum: Int,
     val likeNum: Int,
     val collectNum: Int,
     val isFavorited: Boolean ,
     val isCollected: Boolean,
    val createTime: Long
)

data class Share(
    val collectId: Long,
    val collectNum: Int,
    val content: String,
    val createTime: Long,
    val hasCollect: Boolean,
    val hasFocus: Boolean,
    val hasLike: Boolean,
    val id: Long,
    val imageCode: Long,
    val imageUrlList: List<String>,
    val likeId: Long,
    val likeNum: Int,
    val pUserId: Long,
    val title: String,
    val username: String,
    val avatar:String
)

data class ImageGroup(
    val imageCode:Long,
    val imageUrlList:List<String>,
)

data class Comment(
    val appKey: String,
    val commentLevel: Int,
    val content: String,
    val createTime: String,
    val id: Long,
    val pUserId: Long?,
    val parentCommentId: Long?,
    val parentCommentUserId: Long?,
    val replyCommentId: Long?,
    val replyCommentUserId: Long?,
    val shareId: Long,
    var userName: String
)

