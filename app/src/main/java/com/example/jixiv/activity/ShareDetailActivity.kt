
package com.example.jixiv.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.example.jixiv.components.AvatarWithFrame
import com.example.jixiv.components.Comment
import com.example.jixiv.components.CustomInputKeyboard
import com.example.jixiv.components.ShareDetailBottemBar
import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.utils.saveToPreferences
import com.example.jixiv.utils.timestampToDateTime
import com.example.jixiv.viewModel.ShareDetailViewModel

class ShareDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user: User? = getFromPreferences<User>(this, "user")

        // Handle null user case
        if (user == null) {
            Log.e("ShareDetailActivity", "User is null")
            finish() // Exit the activity or handle the error appropriately
            return
        }
        setContent {
            val share:Share= getFromPreferences<Share>(LocalContext.current,"share")!!
            val viewModel=ShareDetailViewModel(share,user)
                // 这里是你应用的其余部分
            AppTheme {
                ShareDetailScreen(viewModel)
            }
        }
    }
}

@Composable
fun ShareDetailContent(navController: NavController,viewModel: ShareDetailViewModel,share: Share,innerPadding: PaddingValues){
    val pagerState = rememberPagerState(pageCount = { share.imageUrlList.size })
    val fComments by viewModel.FComments.observeAsState()

    Box(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                    item {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp) // Fixed height for pager
                        ) { page ->
                            AsyncImage(
                                model = if(share.imageUrlList.isNotEmpty()) share.imageUrlList[page] else listOf("null"),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    item{
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            PagerIndicator(
                                pagerState = pagerState,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(vertical = 8.dp) // Optional padding
                            )
                        }
                    }
                    item {
                        Panel(share = share)
                    }
                item {
                    FCommentsScreen(navController = navController,viewModel,fComments!!)
                }
            }
        }
    }
}

//指示器部分
@Composable
fun PagerIndicator(pagerState: PagerState, modifier: Modifier = Modifier, dotSpacing: Dp = 3.dp) {
    val pageCount = pagerState.pageCount
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            IndicatorDot(isSelected = isSelected)
        }
    }
}
//指示器部分
@Composable
fun IndicatorDot(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                color = if (isSelected) Color.Black else Color.Gray,
                shape = CircleShape
            )
            .padding(4.dp)
    )
}

@Composable
fun Panel(share: Share) {
val context= LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // 在数据加载完成后才渲染内容
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .clickable {
                        saveToPreferences(context =context,"pUserId",share.pUserId )
                        val intent= Intent(context,MsgActivity::class.java)
                        context.startActivity(intent)
                    }
            ) {
                AvatarWithFrame(share.avatar?:"", size = 45.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = share.username?:"", modifier = Modifier.align(Alignment.CenterVertically))
            }

        Text(text = share.title, modifier = Modifier.padding(vertical = 10.dp),style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text = share.content,style = MaterialTheme.typography.bodyMedium,)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)) {
                        append(timestampToDateTime(share.createTime))
                    }
                },
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDetailScreen(viewModel: ShareDetailViewModel){
    val context= LocalContext.current
    val share by viewModel.share.collectAsState()

    Scaffold(
        Modifier.fillMaxWidth(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding(),
                    navigationIcon={
                        Row (verticalAlignment =Alignment.CenterVertically){
                            IconButton(onClick = {
                                (context as Activity).finish()
                            },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "返回",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth())
            }
        } ,
        bottomBar = {ShareDetailBottemBar(viewModel)}
    ) {innerPadding->
        val navController = rememberNavController()
        Box {
            val sComment by viewModel.SComments.observeAsState(emptyList())
            val checkedFComment by viewModel.checkedFComment.observeAsState()

            NavHost(navController, startDestination = "fCommentScreen") {
                composable(
                    "fCommentScreen",
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                ) {
                        ShareDetailContent(navController,viewModel,share,innerPadding)
                }
                composable(
                    "sCommentScreen",
                    enterTransition = { slideInHorizontally(animationSpec = tween(300)) { it } },
                    exitTransition = { slideOutHorizontally(animationSpec = tween(300)) { it } }
                ) {
                    SCommentsScreen(navController,viewModel = viewModel, comments =sComment ,innerPadding)
                }
            }
        }
    }
    CustomInputKeyboard(viewModel)

}

@Composable
fun FCommentsScreen(navController: NavController, viewModel: ShareDetailViewModel, comments:List<Comment>){
    comments.forEachIndexed{idx,comment->
        Comment(navController,level = 1, comment = comment, viewModel = viewModel)
    }
}

@Composable
fun SCommentsScreen(navController: NavController,viewModel: ShareDetailViewModel,comments:List<Comment>,innerPadding: PaddingValues){
    val fComment by viewModel.checkedFComment.observeAsState()
    LazyColumn(
        Modifier.padding(innerPadding)
    ) {
        item {
            Comment(navController, level = 1,fComment!!,viewModel)
        }
        if(comments.isNotEmpty()){
            items(comments){comment->
                Box(modifier = Modifier.padding(10.dp)){
                    Comment(navController=navController,level = 2, comment = comment, viewModel = viewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShareDetailScreenDemo(){
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
    ShareDetailScreen(viewModel)
}
