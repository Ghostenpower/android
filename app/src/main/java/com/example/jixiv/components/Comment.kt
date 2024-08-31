package com.example.jixiv.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.viewModel.ShareDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun Comment(navController: NavController, level:Int, comment:Comment, viewModel: ShareDetailViewModel){
    val scope= rememberCoroutineScope()
    Column (
        Modifier
            .padding(20.dp)
            .clickable {
                viewModel.updatecheckedFComment(comment)
                viewModel.updateIsVisible()
            }
    ){
        if(level==2){
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray, modifier = Modifier.fillMaxWidth())
        }
        Row {
            Text(
                text = comment.userName,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier
            .fillMaxWidth())
        Box(modifier = Modifier.padding(10.dp)){
            Text(
                text = comment.content,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(text = comment.createTime, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            if(level==1){
                Text(text = "·", fontSize = 40.sp, color = Color.Gray)
                TextButton(onClick = {
                    scope.launch {
                        viewModel.updatecheckedFComment(comment)
                        navController.navigate("sCommentScreen")
                        viewModel.getSecondComments(comment)
                    }
                }) {
                    Text(text = "查看回复", modifier = Modifier.align(Alignment.Top))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FCommentDemo(){
    val context= LocalContext.current
    val comment = Comment(
        appKey = "your_app_key",
        commentLevel = 1,
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
    Comment(navController = NavController(context), level = 1,comment,viewModel)
}