@file:Suppress("DEPRECATION")

package com.example.jixiv.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jixiv.R
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.PageResponse
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import com.example.compose.AppTheme
import com.example.jixiv.components.AvatarWithFrame
import com.example.jixiv.utils.Responses
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.utils.saveToPreferences
import com.example.jixiv.viewModel.ShareViewModel
import com.loren.component.view.composesmartrefresh.MyRefreshFooter
import com.loren.component.view.composesmartrefresh.MyRefreshHeader
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import kotlinx.coroutines.delay

class MainActivity : BaseActivity() {
    val viewModel: ShareViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                    // 这里是你应用的其余部分
                    MainScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateRefresh()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ShareViewModel) {
    val selectedTab by viewModel.selectedTab.observeAsState(initial = 0)
    val tabs = listOf("首页", "我的")
    val context= LocalContext.current
    val drawerStateFlow = viewModel.drawerState.collectAsState().value
    val drawerState = rememberDrawerState(initialValue = drawerStateFlow)

    LaunchedEffect(drawerStateFlow) {
        Log.e("drawerStateFlow", "drawerStateFlow: $drawerStateFlow.", )
        if (drawerStateFlow == DrawerValue.Open) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 处理 Back 事件
            BackHandler(drawerState.isOpen) {
                scope.launch { viewModel.toggleDrawer() }
            }
            // requiredWidth 重置抽屉宽度为指定值，但是不能过大
            ModalDrawerSheet(
                modifier = Modifier.requiredWidth(250.dp)
            ) {
                // 抽屉内容
                DrawerContents(viewModel)
            }
        },
        // 仅在抽屉打开时允许手势开关抽屉，防止手势冲突
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    actions={
                        IconButton(onClick = {
                            scope.launch {
                                val intent=Intent(context,EditActivity::class.java)
                                saveToPreferences<Any?>(context,"share",null)
                                context.startActivity(intent)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "创建图文",tint = Color.White)
                        }
                    },
                    navigationIcon = {
                        // 打开抽屉的按钮
                        IconButton(onClick = {
                            if (drawerStateFlow == DrawerValue.Closed) {
                                scope.launch {
                                    viewModel.toggleDrawer()
                                }
                            }else{
                                scope.launch {
                                    viewModel.toggleDrawer()
                                    delay(50)
                                    viewModel.toggleDrawer()
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "打开抽屉", tint = Color.White)
                        }
                    },
                    title = {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(selectedTab==0){
                                TextButton(
                                    onClick = { viewModel.updateSelectedTab1(0) },
                                    Modifier.fillMaxHeight()
                                ) {
                                    Text("首页",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.updateSelectedTab1(1) },
                                    Modifier.fillMaxHeight()
                                ) {
                                    Text("喜欢",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.updateSelectedTab1(2) },
                                    Modifier.fillMaxHeight()
                                ) {
                                    Text("收藏",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.updateSelectedTab1(3) },
                                    Modifier.fillMaxHeight()
                                ) {
                                    Text("关注",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .systemBarsPadding(),
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorResource(id = R.color.blue)
                ),

                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = colorResource(id = R.color.deep_gray),
                    modifier = Modifier.height(60.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        BottomNavigationItem(
                            icon = {
                                // 根据索引选择相应的图标
                                val icon = when (index) {
                                    0 -> Icons.Filled.Home
                                    1 -> Icons.Filled.Person
                                    else -> Icons.Filled.Help // 处理未知索引
                                }
                                Icon(icon, contentDescription = null, tint = Color.White)
                            },
                            label = { Text(title, color = Color.White) },
                            selected = index == selectedTab,
                            onClick = {
                                // 点击时更新选中的标签
                                viewModel.updateSelectedTab(index)
                            },
                            alwaysShowLabel = true // 始终显示标签
                        )
                    }
                }

            }
        ) { innerPadding ->
            when (selectedTab) {
                0 -> HomeScreen(Modifier.padding(innerPadding),viewModel)
                1 -> ProfileScreen(Modifier.padding(innerPadding),viewModel)
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(viewModel = ShareViewModel())
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier,viewModel: ShareViewModel=ShareViewModel()) {
    Box(modifier.fillMaxSize()) {
        TwoColumnStaggeredGridDemo(viewModel)
    }
}

@Preview
@Composable
fun ProfileScreenDemo(){
    ProfileScreen(Modifier, ShareViewModel())
}
@Composable
fun ProfileScreen(modifier: Modifier = Modifier,viewModel: ShareViewModel=ShareViewModel()) {
    val context= LocalContext.current
    var user: User? = getFromPreferences<User>(context, "user")
    val refresh by viewModel.refresh.observeAsState()

    LaunchedEffect (refresh){
        user = getFromPreferences<User>(context, "user")
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxSize()

        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(colorResource(id = R.color.blue))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarWithFrame(
                    url =user?.avatar?:"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/12/14/b15f3e06-4175-494a-9a81-8ed0b47ae87b.png" ,
                    size =70.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column{
                    Text(maxLines = 1,text = user?.username?:"null", style = MaterialTheme.typography.headlineMedium,)
                    Text(maxLines = 1,text = user?.introduce?:"null", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier
                .height(20.dp)
                .background(Color.LightGray))
            Column() {
                ProfileScreenCard("我的图文",Icons.Filled.Image)
                Divider(color = Color.LightGray,thickness = 0.3.dp)
                ProfileScreenCard("编辑信息",Icons.Filled.Image)
                Divider(color = Color.LightGray,thickness = 0.3.dp)
                ProfileScreenCard("退出登录",Icons.Filled.Image)
                Divider(color = Color.LightGray,thickness = 0.3.dp)
            }
        }
    }
}

@Composable
fun ProfileScreenCard(item:String,imageVector: ImageVector){
    val context= LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                when (item) {
                    "我的图文" -> {
                        val intent = Intent(context, MyActivity::class.java)
                        context.startActivity(intent)
                    }

                    "编辑信息" -> {
                        val intent = Intent(context, MsgActivity::class.java)
                        context.startActivity(intent)
                    }

                    "退出登录" -> {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                }
            } , // 添加点击事件
        elevation= CardDefaults.cardElevation(0.dp),
        shape = RectangleShape
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(58.dp)  // 使 Row 填满 Card
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)  // 添加左右内边距
            ) {
                Icon(
                    imageVector,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = item,
                    style = TextStyle(fontSize = 18.sp)
                )
            }
        }
    }
}

@Composable
fun DrawerContents(viewModel: ShareViewModel){
    val context= LocalContext.current
    val user = getFromPreferences<User>(context = context,"user")
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)

        ){
            AvatarWithFrame(
                url = user!!.avatar?:"https://i2.hdslb.com/bfs/face/00688c18f7d18938cdeeb8a513174d5d259968a6.webp@240w_240h_1c_1s_!web-avatar-nav.avif",
                size = 70.dp
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ){
            DrawerContentsCard("主页",Icons.Filled.Home,viewModel)
            DrawerContentsCard("喜欢",Icons.Filled.Favorite,viewModel)
            DrawerContentsCard("收藏",Icons.Filled.Bookmark,viewModel)
            DrawerContentsCard("测试",Icons.Filled.Bookmark,viewModel)
            DrawerContentsCard("我的",Icons.Filled.Person,viewModel)
        }

    }
}

//抽屉单项卡片
@Composable
fun DrawerContentsCard(item:String,imageVector: ImageVector,viewModel: ShareViewModel){
    val context= LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                when (item) {
                    "首页" -> {
                        viewModel.updateSelectedTab1(0)
                        viewModel.toggleDrawer()
                    }

                    "我的" -> {
                        val intent = Intent(context, MyActivity::class.java)
                        context.startActivity(intent)
                    }

                    "喜欢" -> {
                        viewModel.updateSelectedTab1(1)
                        viewModel.toggleDrawer()
                    }

                    "收藏" -> {
                        viewModel.updateSelectedTab1(2)
                        viewModel.toggleDrawer()
                    }

                    "测试" -> {
                        val intent = Intent(context, TestActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }  // 添加点击事件
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()  // 使 Row 填满 Card
                .padding(horizontal = 8.dp)  // 添加左右内边距
        ) {
            Icon(
                imageVector,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = item,
                style = TextStyle(fontSize = 18.sp)
            )
        }
    }
}

//主页瀑布流
@Composable
fun TwoColumnStaggeredGridDemo(viewModel: ShareViewModel) {
    val e = ExampleRepository()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    val selectTab1 by viewModel.selectedTab1.observeAsState(initial = 0)
    val items by viewModel.shares.observeAsState(emptyList())
    val listState = rememberLazyStaggeredGridState() // or use rememberLazyListState()
    val current by viewModel.currents.observeAsState(1)
    val refreshState = rememberSmartSwipeRefreshState()

    fun callback(result: Result<Responses<PageResponse<List<Share>>>>) {
        if (result.isSuccess) {
            result.getOrNull()?.data?.let { data ->
                data.records?.let { records ->
                    Log.e("callback:records", "callback:records$records ", )
                    if(records.isNotEmpty()){
                        viewModel.updateShares(records)
                    }else{
                        viewModel.updateShares(emptyList())
                    }
                    refreshState.refreshFlag=SmartSwipeStateFlag.SUCCESS
                } ?: run {
                    Log.e("callback", "Data records are null")
                    viewModel.updateShares(emptyList())
                    refreshState.refreshFlag=SmartSwipeStateFlag.ERROR
                }
            } ?: run {
                Log.e("callback", "Data is null")
                viewModel.updateShares(emptyList())
                refreshState.refreshFlag=SmartSwipeStateFlag.ERROR
            }
        } else {
            Log.e("callback", "Result is failure: ${result.exceptionOrNull()}")
            refreshState.refreshFlag=SmartSwipeStateFlag.ERROR
        }
        isLoading = false
        isRefreshing = false
        isLoadingMore = false
    }

    fun fetchData() {
        isLoading = true
        val user: User? = getFromPreferences<User>(context, "user")
        if (user != null) {
            viewModel.updateCurrents(1)
            when (selectTab1) {
                0 -> e.getShareList(current, 10, user.id, ::callback)
                1 -> e.getLikeList(current, 10, user.id, ::callback)
                2 -> e.getCollectedShares(current, 10, user.id, ::callback)
                3->e.getFocusList(current,10,user.id,::callback)
            }
        } else {
            isLoading = false
        }
    }

    fun callback1(result: Result<Responses<PageResponse<List<Share>>>>) {
        if (result.isSuccess) {
            result.getOrNull()?.data?.let { data ->
                data.records?.let { records ->
                    viewModel.addShares(records)
                   refreshState.loadMoreFlag=SmartSwipeStateFlag.SUCCESS
                } ?: run {
                    Log.e("callback", "Data records are null")
                    refreshState.loadMoreFlag=SmartSwipeStateFlag.ERROR
                }
            } ?: run {
                Log.e("callback", "Data is null")
                refreshState.loadMoreFlag=SmartSwipeStateFlag.ERROR
            }
        } else {
            Log.e("callback", "Result is failure: ${result.exceptionOrNull()}")
            refreshState.loadMoreFlag=SmartSwipeStateFlag.ERROR
        }
        isLoading = false
        isRefreshing = false
        isLoadingMore = false
    }

    fun loadMore() {
        if (!isLoadingMore && !isLoading && !isRefreshing) {
            isLoadingMore = true
            // Load more data here, adjust as needed for your pagination
            val user: User? = getFromPreferences<User>(context, "user")
            if (user != null) {
                viewModel.updateCurrents(current+1)
                when (selectTab1) {
                    0 -> e.getShareList(current, 10, user.id, ::callback1) // Example for pagination
                    1 -> e.getLikeList(current, 10, user.id, ::callback1)
                    2 -> e.getCollectedShares(current, 10, user.id, ::callback1)
                }
            } else {
                isLoadingMore = false
            }
        }
    }

    LaunchedEffect(selectTab1) {
        fetchData()
    }

    SmartSwipeRefresh(
        onRefresh = {
            fetchData()
        },
        onLoadMore = {
            loadMore()
        },
        state = refreshState,
        headerIndicator = {
            MyRefreshHeader(refreshState.refreshFlag, true)
        },
        footerIndicator = {
            MyRefreshFooter(refreshState.loadMoreFlag, true)
        }) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (items.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    state = listState,
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(2.dp),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(viewModel.shares.value ?: emptyList()) { index, item ->
                        ShareCard(item = item, index = index)
                    }
                    if (isLoadingMore) {
                        item {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    Image(painter = painterResource(id = R.drawable.empty), contentDescription = "空", Modifier.align(Alignment.Center))
                }
            }
        }
    }

}

//主页图片片分享卡片
@Composable
fun ShareCard(index:Int,item: Share) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(start = 1.dp, end = 1.dp, bottom = 2.dp, top = 1.dp)
            .fillMaxWidth()
            .clickable {
                saveToPreferences(context, "share", item)
                val intent = Intent(context, ShareDetailActivity::class.java)
                intent.putExtra("shareId", item.id)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.outlinedCardElevation(1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            AsyncImage(
                model = item.imageUrlList[0],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.load), // 替换为你的占位符资源
                error = painterResource(R.drawable.failed_to_load) // 替换为你的错误图像资源
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2, // 设置最大显示行数为2
                    overflow = TextOverflow.Ellipsis // 超出部分用省略号表示
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}



