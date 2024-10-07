package com.example.jixiv.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.log

class ShareDetailViewModel(private val initShare: Share,private val initUser:User) : ViewModel() {

    private val e = ExampleRepository()

    private val _share = MutableStateFlow(initShare)
    val share: StateFlow<Share> = _share

    private val _user = MutableStateFlow(initUser)
    val user: StateFlow<User> = _user


    private val _content = MutableLiveData<String>("")
    val content: LiveData<String> = _content
    fun updateContent(content:String){
        _content.value=content
    }


    private val  _isVisible=MutableLiveData(false)
    val isVisible:LiveData<Boolean> = _isVisible
    fun updateIsVisible(){
        _isVisible.value=!_isVisible.value!!
    }




    private val _FComments = MutableLiveData<List<Comment>>(emptyList())
    val FComments: LiveData<List<Comment>> = _FComments
    fun updateFComments(comments:List<Comment>){
        _FComments.value=comments
    }

    private val _SComments = MutableLiveData<List<Comment>>(emptyList())
    val SComments: LiveData<List<Comment>> = _SComments
    fun updateSComments(comments:List<Comment>){
        _SComments.value=comments
    }

    private val comment = Comment(
        appKey = "21e13b398e304338b8198b9dd04c5588",
        commentLevel = 0,
        content = "这是一个示例评论。",
        createTime = "2024-09-25T10:15:30",
        id = 123456789L,
        pUserId = null, // 如果没有父用户ID，可以设置为null
        parentCommentId = null, // 如果没有父评论ID，可以设置为null
        parentCommentUserId = null, // 如果没有父评论用户ID，可以设置为null
        replyCommentId = null, // 如果没有回复评论ID，可以设置为null
        replyCommentUserId = null, // 如果没有回复评论用户ID，可以设置为null
        shareId = 987654321L,
        userName = "用户名"
    )
    private val _checkedFComment = MutableLiveData(comment)
    val checkedFComment: LiveData<Comment> = _checkedFComment
    fun updatecheckedFComment(comment:Comment){
        Log.e("updatecheckedFComment", "updatecheckedFComment: $comment", )
        _checkedFComment.value=comment
    }
    fun updatecheckedFComment(){
        _checkedFComment.value=comment
    }

    init {
        viewModelScope.launch {
            getFirstComments()
//            updatecheckedFComment(_checkedFComment.value!!.copy(shareId = _share.value.id, commentLevel = 0))
        }
    }

    suspend fun getShareDetail() = suspendCancellableCoroutine { continuation ->
        Log.e("getShareDetail", "getShareDetail:${_user.value.id}  ${_share.value.id} ", )
        e.getShareDetail(share.value.id, user.value.id) { res ->
            try {
                if (res.isSuccess) {
                    Log.e("getShareDetail", "getShareDetail:${_user.value.id} $res ${res.getOrNull()?.data} ", )
                    res.getOrNull()?.data?.let {
                        _share.value = it.copy(avatar = _share.value.avatar)
                    }
                    continuation.resume(Unit)  // 成功时，恢复协程并传递 Unit
                } else {
                    continuation.resumeWithException(Throwable("Failed to get share detail"))  // 失败时，恢复协程并抛出异常
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)  // 捕获异常并恢复协程
            }
        }
    }

    suspend fun addFirstComment(): Boolean = suspendCancellableCoroutine { continuation ->
        e.commentFirst(_content.value!!, share.value.id, user.value.id, user.value.username) { result ->
            try {
                val isSuccess = result.isSuccess && result.getOrNull()?.code == 200
                continuation.resume(isSuccess)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }


    suspend fun like(): Boolean = suspendCancellableCoroutine { continuation ->
        val user=_user.value
        e.like(initShare.id, user.id) { res ->
            try {
                if (res.isSuccess) {
                    val isSuccess = res.getOrNull()?.code == 200
                    continuation.resume(isSuccess)
                } else {
                    continuation.resume(false)
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun dislike(): Boolean = suspendCancellableCoroutine { continuation ->
        e.likeCancel(share.value.likeId) { res ->
            try {
                if (res.isSuccess) {
                    val isSuccess = res.getOrNull()?.code == 200
                    // 先恢复当前协程
                    continuation.resume(isSuccess)
                } else {
                    continuation.resume(false)
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun collect(): Boolean = suspendCancellableCoroutine { continuation ->
        val user=_user.value
        e.collect(initShare.id, user.id) { res ->
            try {
                val isSuccess = res.isSuccess && res.getOrNull()?.code == 200
                continuation.resume(isSuccess)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        getShareDetail()
                    } catch (e: Exception) {
                        // 处理异常
                        // 这里可以记录日志或其他异常处理逻辑
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun discollect(): Boolean = suspendCancellableCoroutine { continuation ->
        e.collectCancel(share.value.collectId) { res ->
            try {
                val isSuccess = res.isSuccess && res.getOrNull()?.code == 200
                continuation.resume(isSuccess)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        getShareDetail()
                    } catch (e: Exception) {
                        // 处理异常
                        // 这里可以记录日志或其他异常处理逻辑
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun getFirstComments()= suspendCancellableCoroutine {continuation->
        Log.e("", "getFirstComments: ", )
        e.getFirstComment(null,_share.value.id,null) { result ->
            try {
                result.onSuccess { response ->
                    val comments = response.data.records
                    comments.let {
                        if (it.isNotEmpty()){
                            updateFComments(comments)
                            Log.e("getFirstComments", "getFirestComments: $comments", )
                            continuation.resume(Unit)
                        }else{
                            updateFComments(emptyList())
                        }
                    }
                }
                result.onFailure { throwable ->
                    Log.e("updateFComment", "updateFComment:$throwable ", )
                    continuation.resumeWithException(Throwable("Failed to get share comment"))  // 失败时，恢复协程并抛出异常
                }
            }catch (e:Exception){
                continuation.resumeWithException(Throwable("Failed to get share comment"))  // 失败时，恢复协程并抛出异常
            }
        }
    }

    suspend fun getSecondComments(comment: Comment)= suspendCancellableCoroutine {continuation->
        e.getSecondComment(comment.id,null,comment.shareId,null) { result ->
            try {
                result.onSuccess { response ->
                    val comments = response.data.records
                    comments.let {
                        if (it.isNotEmpty()){
                            updateSComments(comments)
                            continuation.resume(Unit)
                        }else{
                            updateSComments(emptyList())
                        }
                    }
                }
                result.onFailure { throwable ->
                    Log.e("updateFComment", "updateFComment:$throwable ", )
                    continuation.resumeWithException(Throwable("Failed to get share comment"))  // 失败时，恢复协程并抛出异常
                }
            }catch (e:Exception){
                continuation.resumeWithException(Throwable("Failed to get share comment"))  // 失败时，恢复协程并抛出异常
            }
        }
    }

    suspend fun addComment(context: Context)= suspendCancellableCoroutine{ continuation->
        val comment:Comment=_checkedFComment.value!!
        when(_checkedFComment.value!!.commentLevel){
            0->{
                e.commentFirst(_content.value!!,_share.value.id,_user.value.id,_user.value.username){result->
                    try {
                        result.onSuccess { response ->
                            val code = response.code
                            if(code==200){
                                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show()
                                continuation.resume(Unit)
                            }else{
                                Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                        result.onFailure {
                            continuation.resumeWithException(Throwable("Failed to add share FComment"))  // 失败时，恢复协程并抛出异常
                        }
                        _isVisible.value=false
                    }catch (e:Exception){
                        continuation.resumeWithException(Throwable("Failed to add share FComment"))  // 失败时，恢复协程并抛出异常
                    }
                }
            }
            1,2->{
                Log.e("addComment", "addComment: $comment", )
                e.commentSecond(_content.value!!,comment.parentCommentId?:comment.id,comment.parentCommentUserId?:comment.pUserId!!,comment.id,comment.pUserId!!,_share.value.id,_user.value.id,_user.value.username){result->
                    try {
                        result.onSuccess { response ->
                            val code = response.code
                            if(code==200){
                                Toast.makeText(context, "回复成功", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context, "回复失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                        result.onFailure { throwable ->
                            continuation.resumeWithException(Throwable("Failed to add share FComment"))  // 失败时，恢复协程并抛出异常
                        }
                    }catch (e:Exception){
                        continuation.resumeWithException(Throwable("Failed to add share FComment"))  // 失败时，恢复协程并抛出异常
                    }
                }
            }
        }
    }
}

