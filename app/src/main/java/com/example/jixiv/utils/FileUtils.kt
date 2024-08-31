package com.example.jixiv.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File

object FileUtils {

    /**
     * 根据 URI 获取真实的文件路径。
     *
     * @param context 上下文
     * @param uri 文件的 URI
     * @return 文件的真实路径，如果无法获取则返回 null
     */
    fun getFilePathByUri(context: Context?, uri: Uri?): String? {
        if (context == null || uri == null) return null

        // 处理文件 URI
        if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        // 处理内容 URI
        if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                return handleDocumentUri(context, uri)
            } else {
                return getDataColumn(context, uri)
            }
        }

        return null
    }

    private fun handleDocumentUri(context: Context, uri: Uri): String? {
        when {
            isLocalStorageDocument(uri) -> {
                return DocumentsContract.getDocumentId(uri)
            }
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return when (type) {
                    "primary" -> getExternalStoragePath(context, split[1])
                    "home" -> getExternalStoragePath(context, "documents/${split[1]}")
                    else -> getExternalStoragePath(context, "documents/${split[1]}")
                }
            }
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.substring(4)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return getDownloadsPath(context, id)
                }
                return getDataColumn(context, uri)
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri: Uri? = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, "_id=?", selectionArgs)
            }
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String? = null, selectionArgs: Array<String>? = null): String? {
        uri ?: return null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    private fun getDownloadsPath(context: Context, id: String): String? {
        val contentUriPrefixesToTry = arrayOf(
            "content://downloads/public_downloads",
            "content://downloads/my_downloads",
            "content://downloads/all_downloads"
        )
        for (contentUriPrefix in contentUriPrefixesToTry) {
            val contentUri = Uri.withAppendedPath(Uri.parse(contentUriPrefix), id)
            val path = getDataColumn(context, contentUri)
            if (!TextUtils.isEmpty(path)) {
                return path
            }
        }
        return null
    }

    private fun getExternalStoragePath(context: Context, path: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(null)?.toString() + File.separator + path
        } else {
            Environment.getExternalStorageDirectory().toString() + File.separator + path
        }
    }

    private fun isLocalStorageDocument(uri: Uri) = "com.android.externalstorage.documents".equals(uri.authority, ignoreCase = true)
    private fun isExternalStorageDocument(uri: Uri) = "com.android.providers.downloads.documents".equals(uri.authority, ignoreCase = true)
    private fun isDownloadsDocument(uri: Uri) = "com.android.providers.media.documents".equals(uri.authority, ignoreCase = true)
    private fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents".equals(uri.authority, ignoreCase = true)
    private fun isGooglePhotosUri(uri: Uri) = "com.google.android.apps.photos.contentprovider".equals(uri.authority, ignoreCase = true)
}