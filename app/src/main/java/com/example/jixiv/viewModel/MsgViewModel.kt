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
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.utils.saveToPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MsgViewModel(pUserId:Long):ViewModel(){
    private val _user= MutableLiveData(User("",0L,"","",0,"","",0L,0L))
    val user:LiveData<User> = _user

    val e= ExampleRepository()

    fun updateUser(context: Context){
        val newData:User?=getFromPreferences(context,"user")
        if(newData!=null){
            _user.value=newData
        }
    }

    private val _pUserId=MutableLiveData(pUserId)
    val pUserId:LiveData<Long> =_pUserId
    fun updatePUserId(pUserId: Long){
        _pUserId.value=pUserId
    }


    fun updateUserName(name:String){
        _user.value=_user.value!!.copy(username = name)
    }
    fun updatecontent(introduce:String){
        _user.value=_user.value!!.copy(introduce = introduce)
    }
    fun updateSex(sex:Int){
        _user.value=_user.value!!.copy(sex = sex)
    }
    fun updateAvatar(context: Context,avatar:String){
        _user.value=_user.value!!.copy(avatar=avatar)
        saveToPreferences(context,"user",_user.value)
    }

    private var _imgUriList=MutableLiveData<List<Uri>>(emptyList())
    val imgUriList:LiveData<List<Uri>> =_imgUriList

    private  val _selecting=MutableLiveData<Boolean>(false)
    val selecting:LiveData<Boolean> = _selecting
    fun updateSelecting(select:Boolean){
        _selecting.value=select
    }

    fun addUriToUriList(uri: Uri) {
        val updatedList = _imgUriList.value.orEmpty().toMutableList().apply {
            add(uri)
        }
        _imgUriList.value = updatedList
        Log.e("addUriToUriList", "addUriToUriList: ${_imgUriList.value}", )
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
            Log.e("EditimageUpload", "EditimageUpload: $res", )
            if (continuation.isActive) {
                try {
                    if (res.isSuccess) {
                        res.getOrNull()?.data?.let {
                            it.imageUrlList?.get(0)?.let { string->
                                updateAvatar(context,string)
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
    fun prepareFilePart(uri: Uri, context: Context): MultipartBody.Part {
        val file = File(FileUtils.getFilePathByUri(context,uri)!!)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        return MultipartBody.Part.createFormData("fileList", file.name, requestFile)
    }
    fun createMultipartBodyParts(uriList: List<Uri>, context: Context): List<MultipartBody.Part> {
        return uriList.map { uri -> prepareFilePart(uri, context) }
    }

    suspend fun editUserMsg(context: Context):Unit= suspendCancellableCoroutine { continuation->
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

        fun callback(res: Result<Responses<String>>) {
            if (continuation.isActive) {
                try {
                    if (res.isSuccess) {
                        res.getOrNull()?.let {
                            Log.e("callback", "callback: $it")
                            val message = if (it.code == 200) {
                                "编辑成功"

                            } else {
                                "编辑失败"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if(it.code==200){
                                saveToPreferences(context,"user",_user.value!!.copy(lastUpdateTime = System.currentTimeMillis()))
                                (context as? Activity)?.finish()
                            }
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

        e.update(user = _user.value!!,::callback)
    }


}