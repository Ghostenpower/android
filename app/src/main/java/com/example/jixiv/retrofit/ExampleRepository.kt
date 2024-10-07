package com.example.jixiv.retrofit

import android.util.Log
import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.CoverShare
import com.example.jixiv.utils.ImageGroup
import com.example.jixiv.utils.PageResponse
import com.example.jixiv.utils.RegisterRequest
import com.example.jixiv.utils.Responses
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.ShareDetail
import com.example.jixiv.utils.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class ExampleRepository {
    private val apiService = ApiServiceProvider.apiService

    fun update(user:User,callback: (Result<Responses<String>>)-> Unit){
        Log.e("updateUserMsg", "updateUserMsg: $user", )
        val request=ApiService.UserUpdateRequest(user.avatar,user.id,user.introduce,user.sex,user.username)
        apiService.userUpdate(request).enqueue(object :Callback<Responses<String>>{
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<String> = response.body()!!
                    callback(Result.success(responseBody))
                } else {
                    callback(Result.failure(Exception("Network error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    suspend fun login(username: String, password: String): Result<Responses<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(username, password).awaitResponse() // 需要使用适当的扩展函数
                if (response.isSuccessful) {
                    val responseBody: Responses<User> = response.body()!!
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Network error: ${response.code()}"))
                }
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    fun register(password: String,username: String,callback: (Result<Int>) -> Unit){
        Log.e("TAG", "register: $username $password", )
        val request = RegisterRequest(username, password)
        apiService.register(username,password).enqueue(object :Callback<Responses<String>>{
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                if (response.isSuccessful) {
                    Log.e("register", "onResponse: $response")
                    val responseBody: Responses<String> = response.body()!!
                    Log.e("responseBody", "onResponse: $responseBody", )
                    callback(Result.success(responseBody.code))
                } else {
                    Log.e("register", "onResponse: failure")
                    callback(Result.failure(Exception("Network error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("register", "onResponse: onFailure")
                callback(Result.failure(t))
            }
        })
    }

    fun getUserByName(username: String, callback: (Result<Responses<User>>) -> Unit) {
        Log.e("getUserByName", "Requesting user by name: $username")

        apiService.getUserByName(username).enqueue(object : Callback<Responses<User>> {
            override fun onResponse(
                call: Call<Responses<User>>,
                response: Response<Responses<User>>
            ) {
                Log.e("getUserByName", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<User>? = response.body()
                    Log.e("getUserByName", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("getUserByName", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<User>>, t: Throwable) {
                Log.e("getUserByName", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun shareSave(content: String, imageCode: Long, pUserId: Long, title: String, callback: (Result<Responses<String>>) -> Unit) {
        val request = ApiService.ShareSaveRequest(content, imageCode, pUserId, title)
        apiService.shareSave(request).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("shareSave", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("shareSave", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("shareSave", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("shareSave", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun shareGetSave(current: Int?, size: Int?, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        Log.e("shareGetSave", "Fetching shares for user ID: $userId")

        apiService.shareGetSave(current,size,userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                Log.e("shareGetSave", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>>? = response.body()
                    Log.e("shareGetSave", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("shareGetSave", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                Log.e("shareGetSave", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun getMyselfShare(current: Int?, size: Int?, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        Log.e("getMyselfShare", "Fetching shares for user ID: $userId")

        apiService.getMyselfShare(current, size, userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                Log.e("getMyselfShare", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>>? = response.body()
                    Log.e("getMyselfShare", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("getMyselfShare", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                Log.e("getMyselfShare", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun getShareDetail(shareId: Long, userId: Long, callback: (Result<Responses<Share>>) -> Unit) {
        Log.e("getShareDetail", "Fetching details for share ID: $shareId")

        apiService.getShareDetail(shareId, userId).enqueue(object : Callback<Responses<Share>> {
            override fun onResponse(
                call: Call<Responses<Share>>,
                response: Response<Responses<Share>>
            ) {
                Log.e("getShareDetail", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<Share>? = response.body()
                    Log.e("getShareDetail", "Response body: $responseBody")
                        callback(Result.success(responseBody!!.copy(data = responseBody.data.copy(username = responseBody.data.username?:"null", avatar = responseBody.data.avatar?:"null"))))
                } else {
                    Log.e("getShareDetail", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<Share>>, t: Throwable) {
                Log.e("getShareDetail", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun deleteShare(shareId: Long, userId: Long, callback: (Result<Responses<String>>) -> Unit) {
        Log.e("deleteShare", "Deleting share with ID: $shareId for user ID: $userId")

        apiService.deleteShare(shareId, userId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("deleteShare", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("deleteShare", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("deleteShare", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("deleteShare", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun changeShare(content: String, id: Long, imageCode: Long, pUserId: Long, title: String, callback: (Result<Responses<String>>) -> Unit) {
        val request = ApiService.ChangeShareRequest(content, id, imageCode, pUserId, title)
        Log.e("changeShare", "Changing share with ID: $id")
        apiService.changeShare(request).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("changeShare", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("changeShare", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("changeShare", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("changeShare", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun addShare(content: String, id: Long?, imageCode: Long, pUserId: Long, title: String, callback: (Result<Responses<String>>) -> Unit) {
        val request = ApiService.ShareRequest(content, id, imageCode, pUserId, title)

        // 调用 API
        apiService.addShare(request).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("addShare", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("addShare", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("addShare", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("addShare", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun getShareList(current: Int, size: Int, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        Log.e("getShareList", "Fetching share list for user ID: $userId with pagination: current=$current, size=$size")

        apiService.getShareList(current, size, userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                Log.e("getShareList", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>> = response.body()!!
                    Log.e("getShareList", "Response body: $responseBody")
                    callback(Result.success(responseBody))
                } else {
                    Log.e("getShareList", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                Log.e("getShareList", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun likeCancel(likeId: Long, callback: (Result<Responses<String>>) -> Unit) {
        Log.e("likeCancel", "Cancelling like with ID: $likeId")

        apiService.likeCancel(likeId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("likeCancel", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("likeCancel", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("likeCancel", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("likeCancel", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun like(shareId: Long, userId: Long, callback: (Result<Responses<String>>) -> Unit) {
        Log.e("like", "Liking share with ID: $shareId for user ID: $userId")

        apiService.like(shareId, userId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("like", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("like", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("like", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("like", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun getLikeList(current: Int, size: Int, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        Log.e("getLikeList", "Fetching share list for user ID: $userId with pagination: current=$current, size=$size")

        apiService.getLikeList(current, size, userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                Log.e("getLikeList", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>> = response.body()!!
                    Log.e("getLikeList", "Response body: $responseBody")
                    callback(Result.success(responseBody))
                } else {
                    Log.e("getLikeList", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                Log.e("getLikeList", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun imageUpload(fileList: List<MultipartBody.Part>, callback: (Result<Responses<ImageGroup>>) -> Unit) {
        Log.e("imageUpload", "Uploading ${fileList.size} images")
        apiService.imageUpload(fileList).enqueue(object : Callback<Responses<ImageGroup>> {
            override fun onResponse(
                call: Call<Responses<ImageGroup>>,
                response: Response<Responses<ImageGroup>>
            ) {
                Log.e("imageUpload", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<ImageGroup>? = response.body()
                    Log.e("imageUpload", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("imageUpload", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
                callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
            }
            override fun onFailure(call: Call<Responses<ImageGroup>>, t: Throwable) {
                Log.e("imageUpload", "Error: $t")
                callback(Result.failure(t))
            }

        })
//        apiService.imageUpload(fileList).enqueue(object : Callback<Responses<ImageGroup>> {
//            override fun onResponse(
//                call: Call<Responses<ImageGroup>>,
//                response: Response<Responses<ImageGroup>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.e("imageUpload", "Response body: ${response.body()}")
//                    callback(Result.success(response.body()!!))
//                } else {
//                    Log.e("imageUpload", "Response error: ${response.errorBody()?.string()}")
//                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
//                }
//            }
//
//            override fun onFailure(call: Call<Responses<ImageGroup>>, t: Throwable) {
//                Log.e("imageUpload", "Error: $t")
//                callback(Result.failure(t))
//            }
//        })
    }

    fun focusCancel(focusUserId: Long, userId: Long, callback: (Result<Responses<String>>) -> Unit) {
        Log.e("focusCancel", "Cancelling focus for user ID: $focusUserId by user ID: $userId")

        apiService.focusCancel(focusUserId, userId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("focusCancel", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    Log.e("focusCancel", "Response body: $responseBody")
                    callback(Result.success(responseBody!!))
                } else {
                    Log.e("focusCancel", "Response error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                Log.e("focusCancel", "Error: $t")
                callback(Result.failure(t))
            }
        })
    }

    fun focus(focusUserId: Long, userId: Long, callback: (Result<Responses<String>>) -> Unit) {
        apiService.focus(focusUserId, userId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getFocusList(current: Int, size: Int, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        apiService.getFocusList(current, size, userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun commentSecond(content: String, parentCommentId: Long,parentCommentUserId: Long, replyCommentId: Long, replyCommentUserId: Long, shareId: Long, userId: Long, userName: String, callback: (Result<Responses<String>>) -> Unit) {
        val request = ApiService.SecondCommentRequest(content, parentCommentId, parentCommentUserId, replyCommentId,replyCommentUserId,shareId,userId,userName)
        apiService.commentSecond(request).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                val result = if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Response error: ${response.errorBody()?.string()}"))
                }
                callback(result)
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getSecondComment(commentId: Long, current: Int?=null, shareId: Long, size: Int?=null, callback: (Result<Responses<PageResponse<List<Comment>>>>) -> Unit) {
        val call = if (size != null && current!=null) {
            apiService.getSecondComment(commentId,current,shareId,size)
        } else {
            apiService.getSecondComment(commentId,null,shareId,null) // 这是没有 size 参数的版本
        }
        call.enqueue(object : Callback<Responses<PageResponse<List<Comment>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Comment>>>>,
                response: Response<Responses<PageResponse<List<Comment>>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Comment>>>? = response.body()
//
//                    // 检查 responseBody 是否为 null
                    if (responseBody != null) {
                        // 获取 PageResponse 对象
                        val pageResponse = responseBody.data

                        // 检查 pageResponse 是否为 null
                        if (pageResponse != null) {
                            // 确保 PageResponse 的 records 不是 null
                            val records = pageResponse.records ?: emptyList() // 如果 records 为 null，则使用空列表
                            val current = pageResponse.current ?: 0
                            val size = pageResponse.size ?: 0
                            val total = pageResponse.total ?: 0

                            // 使用非 null 的 pageResponse 调用 copy 方法
                            val updatedPageResponse = pageResponse.copy(records = records, current = current, total = total, size = size)
                            val updatedResponseBody = responseBody.copy(data = updatedPageResponse)

                            callback(Result.success(updatedResponseBody))
                        } else {
                            // 处理 pageResponse 为 null 的情况
                            val updatedResponseBody = responseBody.copy(data = PageResponse(0,0,0, emptyList()))

                            callback(Result.success(updatedResponseBody))
                        }
                    } else {
                        // 处理 responseBody 为 null 的情况
                        Log.e("ExampleRepository", "ResponseBody is null")
                        callback(Result.failure(Exception("ResponseBody is null")))
                    }

                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Comment>>>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun commentFirst(content: String, shareId: Long, userId: Long, userName: String, callback: (Result<Responses<String>>) -> Unit) {
        Log.e("commentFirst", "commentFirst: $content, $shareId, $userId, $userName")

        val request = ApiService.FirstCommentRequest(content, shareId, userId, userName)

        apiService.commentFirst(request).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("commentFirst", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getFirstComment(current: Int?=null, shareId: Long, size: Int?=null, callback: (Result<Responses<PageResponse<List<Comment>>>>) -> Unit) {
        val call = if (size != null && current!=null) {
            apiService.getFirstComment(current, shareId, size)
        } else {
            apiService.getFirstComment(null,shareId,null) // 这是没有 size 参数的版本
        }
            call.enqueue(object : Callback<Responses<PageResponse<List<Comment>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Comment>>>>,
                response: Response<Responses<PageResponse<List<Comment>>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Comment>>>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Comment>>>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun collectCancel(collectId: Long, callback: (Result<Responses<String>>) -> Unit) {
        apiService.collectCancel(collectId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun collect(shareId: Long, userId: Long, callback: (Result<Responses<String>>) -> Unit) {
        apiService.collect(shareId, userId).enqueue(object : Callback<Responses<String>> {
            override fun onResponse(
                call: Call<Responses<String>>,
                response: Response<Responses<String>>
            ) {
                Log.e("collect", "collect: $response", )
                if (response.isSuccessful) {
                    val responseBody: Responses<String>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getCollectedShares(current: Int, size: Int, userId: Long, callback: (Result<Responses<PageResponse<List<Share>>>>) -> Unit) {
        apiService.collect(current, size, userId).enqueue(object : Callback<Responses<PageResponse<List<Share>>>> {
            override fun onResponse(
                call: Call<Responses<PageResponse<List<Share>>>>,
                response: Response<Responses<PageResponse<List<Share>>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: Responses<PageResponse<List<Share>>>? = response.body()
                    callback(Result.success(responseBody!!))
                } else {
                    callback(Result.failure(Exception("Response error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<Responses<PageResponse<List<Share>>>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

}


