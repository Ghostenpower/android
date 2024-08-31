package com.example.jixiv.viewModel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.FileUtils
import com.example.jixiv.utils.Responses
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TestViewModel: ViewModel() {
    private var _imgUriList= MutableLiveData<List<Uri>>(emptyList())
    val imgUriList: LiveData<List<Uri>> =_imgUriList

    fun addUriToUriList(uri: Uri) {
        val updatedList = _imgUriList.value.orEmpty().toMutableList().apply {
            add(uri)
        }
        _imgUriList.value = updatedList
        Log.e("addUriToUriList", "addUriToUriList: ${_imgUriList.value}", )
    }

    fun deleteUriFromList(idx:Int){
        val updatedList = _imgUriList.value.orEmpty().toMutableList()
        if (idx in updatedList.indices) {
            updatedList.removeAt(idx)
        } else {
            println("Index out of bounds")
        }
        _imgUriList.value = updatedList
    }

    private val _share = MutableLiveData(Share(0L,0,"",0L,false,false,false,0L,0L, listOf(),0L,0,0L,"","",""))
    val share: LiveData<Share> = _share

    fun updateShare(share: Share){
        if(share!=null){
            _share.value=share
        }
    }

    fun upDateShareTitle(title:String){
        _share.value=_share.value!!.copy(title=title)
    }

    fun upDateShareContent(content:String){
        _share.value=_share.value!!.copy(content=content)
    }

    fun updateShareImageCode(imagecode:Long){
        _share.value=_share.value!!.copy(imageCode=imagecode)
    }

    val e= ExampleRepository()

    private val _isSave = MutableLiveData(false)
    val isSave: LiveData<Boolean> = _isSave

    fun updateIsSave(issave:Boolean){
        _isSave.value=issave
    }

    suspend fun imageUpload(context: Context): Unit = suspendCancellableCoroutine { continuation ->
        var isResumed = false

        fun resumeSafely(value: Unit? = null, exception: Throwable? = null) {
            if (!isResumed) {
                isResumed = true
                if (exception != null) {
                    continuation.resumeWithException(exception)
                } else {
                    continuation.resume(value ?: Unit)
                }
            }
        }

        val uris: List<Uri> = _imgUriList.value ?: emptyList()
        val parts = createMultipartBodyParts(uris, context)

        e.imageUpload(parts) { res ->
            if (continuation.isActive) {
                try {
                    if (res.isSuccess) {
                        res.getOrNull()?.data?.let {
                            it.imageCode?.let { code ->
                                updateShareImageCode(code)
                                Log.e("addShareimageCode", "imageUpload: ${_share.value!!.imageCode}", )
                            }
                        }
                        resumeSafely()  // 成功时，恢复协程
                    } else {
                        resumeSafely(exception = Throwable("Failed to get share upload"))  // 失败时，恢复协程并抛出异常
                    }
                } catch (e: Exception) {
                    resumeSafely(exception = e)  // 捕获异常并恢复协程
                }
            }
        }
    }


    suspend fun addShare(context: Context) = suspendCancellableCoroutine<Unit> { continuation ->
        var isResumed = false
        fun resumeSafely(value: Unit? = null, exception: Throwable? = null) {
            if (!isResumed) {
                isResumed = true
                if (exception != null) {
                    continuation.resumeWithException(exception)
                } else {
                    continuation.resume(value ?: Unit)
                }
            }
        }

        try {
            fun callback(res: Result<Responses<String>>) {
                if (continuation.isActive) {
                    try {
                        if (res.isSuccess) {
                            res.getOrNull()?.let {
                                Log.e("callback", "callback: $it")
                                val message = if (it.code == 200) {
                                    if (isSave.value == false) "发布成功" else "保存成功"
                                } else {
                                    if (isSave.value == false) "发布失败" else "保存失败"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if(it.code==200)(context as? Activity)?.finish()
                                resumeSafely() // 成功时，恢复协程
                            } ?: resumeSafely(exception = Throwable("Response is null")) // 如果结果为空
                        } else {
                            resumeSafely(exception = Throwable("Failed to add share")) // 失败时，恢复协程并抛出异常
                        }
                    } catch (e: Exception) {
                        resumeSafely(exception = e) // 捕获异常并恢复协程
                    }
                }
            }

            val user: User = getFromPreferences(context, "user")!!
            if (isSave.value == false) {
                Log.e("addShareimageCode", "addShareimageCode ${_share.value!!.imageCode}", )
                e.addShare(
                    _share.value!!.content,
                    if (share.value!!.id == 0L) null else share.value!!.id,
                    _share.value!!.imageCode,
                    if (share.value!!.pUserId == 0L) user.id else share.value!!.id,
                    _share.value!!.title,
                    ::callback
                )
            } else {
                Log.e("addShareimageCode", "addShareimageCode ${_share.value}", )
                e.shareSave(
                    _share.value!!.content,
                    _share.value!!.imageCode,
                    if (share.value!!.pUserId == 0L) user.id else share.value!!.id,
                    _share.value!!.title,
                    ::callback
                )
            }
        } catch (e: Exception) {
            resumeSafely(exception = e) // 捕获异常并恢复协程
        }
    }


    fun prepareFilePart(uri: Uri, context: Context): MultipartBody.Part {
        val file = File(FileUtils.getFilePathByUri(context,uri)!!)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        return MultipartBody.Part.createFormData("fileList", file.name, requestFile)
    }

    fun createMultipartBodyParts(uriList: List<Uri>, context: Context): List<MultipartBody.Part> {
        return uriList.map { uri -> prepareFilePart(uri, context) }
    }
}