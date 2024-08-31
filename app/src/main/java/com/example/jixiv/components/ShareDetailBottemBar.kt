package com.example.jixiv.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.viewModel.ShareDetailViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun ShareDetailBottemBar(viewModel: ShareDetailViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val shareDetial by viewModel.share.collectAsState()

    HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth())
    Box(modifier = Modifier.padding(10.dp)){
        Row (
        ){
            Button(
                onClick = { viewModel.updateIsVisible() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray, // 设置背景为透明
                    contentColor = Color.Black // 设置内容颜色
                ),
                elevation = null, // 移除阴影
                border = null ,
                modifier = Modifier.weight(6f)
            ) {
                // 按钮内容
                Text("评论", color = Color.Gray.copy(alpha = 0.2f))
            }
            IconButton(onClick = { coroutineScope.launch {
                if(shareDetial.hasLike){viewModel.dislike();viewModel.getShareDetail()} else {viewModel.like();viewModel.getShareDetail()}
            } }, modifier = Modifier
                .weight(1f)) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "like", tint = if(shareDetial!!.hasLike==true) Color.Red else Color.Gray)
            }
            IconButton(onClick = { coroutineScope.launch {
                if(shareDetial.hasCollect){viewModel.discollect();viewModel.getShareDetail()} else {viewModel.collect();viewModel.getShareDetail()}
            } }, modifier = Modifier
                .weight(1f)) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = "collect", tint = if(shareDetial!!.hasCollect==true) Color.Red else Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShareDetailBottomBar(){
    val user = User(
        appKey = "my_app_key",
        id = 1L,
        username = "exampleUser",
        password = "securePassword",
        sex = 1, // 1 表示男性，0 表示女性，-1 表示未指定
        introduce = "Hello, I'm a user!",
        avatar = "http://example.com/avatar.jpg",
        createTime = System.currentTimeMillis(),
        lastUpdateTime = System.currentTimeMillis()
    )
    val share = Share(
        collectId = 123L,
        collectNum = 10,
        content = "This is a shared content.",
        createTime = System.currentTimeMillis(),
        hasCollect = true,
        hasFocus = false,
        hasLike = true,
        id = 456L,
        imageCode = 789L,
        imageUrlList = listOf("http://example.com/image1.jpg", "http://example.com/image2.jpg"),
        likeId = 101112L,
        likeNum = 5,
        pUserId = 131415L,
        title = "Share Title",
        username = "exampleUser",
        avatar = "http://example.com/avatar.jpg"
    )
    val viewModel=ShareDetailViewModel(share,user)
    ShareDetailBottemBar(viewModel = viewModel)
}