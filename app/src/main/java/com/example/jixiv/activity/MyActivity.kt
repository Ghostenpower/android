package com.example.jixiv.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.jixiv.activity.BaseActivity
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.jixiv.R
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.PageResponse
import com.example.jixiv.utils.Responses
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.utils.saveToPreferences
import com.example.jixiv.viewModel.MyViewModel
import com.example.jixiv.viewModel.MyViewModelFactory
import com.example.jixiv.viewModel.ShareDetailViewModel
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.log

val LocalMyViewModel = compositionLocalOf<MyViewModel> {
    error("No MyViewModel provided")
}
class MyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MyViewModel by viewModels {
                MyViewModelFactory()
            }
            CompositionLocalProvider(LocalMyViewModel provides viewModel) {
                // 这里是你应用的其余部分
                Box{
                    MyScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(){
    val context= LocalContext.current
    val viewModel= LocalMyViewModel.current
    val isEdit by viewModel.isEdit.observeAsState()
    val pageTab by viewModel.pageTab.observeAsState()
    val saveLoading by viewModel.isLoading.observeAsState()

    if(saveLoading==true){
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 1f)))
    }
    CenterAlignedTopAppBar(
        title = {
            if(isEdit==false){
                Row {
                    androidx.compose.material.TextButton(
                        onClick = { viewModel.updatePageTab(0) },
                        Modifier.fillMaxHeight()
                    ) {
                        Text(
                            "已发布",
                            color = Color.White,
                            fontSize = 20.sp,
                        )
                    }
                    androidx.compose.material.TextButton(
                        onClick = { viewModel.updatePageTab(1) },
                        Modifier.fillMaxHeight()
                    ) {
                        Text(
                            "已保存",
                            color = Color.White,
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        },
        navigationIcon={
            if(isEdit==false){
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription ="back", tint = Color.White )
                }
            }else{
                TextButton(onClick = {
                    viewModel.selectAll()
                }) {
                    Text(text = "全选",style = TextStyle(Color.White))
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.blue)),
        actions = {
            if(isEdit == false){
                IconButton(onClick = { viewModel.updateSelectedTab(true) }) {
                    Icon(Icons.Filled.Edit, contentDescription ="back", tint = Color.White )
                }
            }else{
                TextButton(onClick = {
                    viewModel.deleteSeleted()
                }) {
                    Text(text = "删除",style = TextStyle(Color.White))
                }
                if(pageTab==1){
                    TextButton(onClick = {
                        viewModel.release()
                    }) {
                        Text(text = "发布", style = TextStyle(Color.White))
                    }
                }
                TextButton(onClick = {
                    viewModel.updateSelectedTab(false)
                    viewModel.resetSelectList()
                }) {
                    Text(text = "取消", style = TextStyle(Color.White))
                }
            }

        },
    )
    TwoColumn()
}

@Composable
fun TwoColumn() {
    val e = ExampleRepository()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val viewModel = LocalMyViewModel.current
    val items by viewModel.shares.observeAsState(emptyList())
    val user: User? = getFromPreferences<User>(context, "user")
    val pageTab by viewModel.pageTab.observeAsState()
    val saveLoading by viewModel.isLoading.observeAsState()


    fun callback(result: Result<Responses<PageResponse<List<Share>>>>) {
        if (result.isSuccess) {
            result.getOrNull()?.data?.let { data ->
                data.records?.let {
                    if(it.isNotEmpty())viewModel.updateShares(it)
                    else viewModel.updateShares(emptyList())
                } ?: run {
                    Log.e("callback", "Data records are null")
                    viewModel.updateShares(emptyList())
                }
            } ?: run {
                Log.e("callback", "Data is null")
                viewModel.updateShares(emptyList())
            }
        } else {
            Log.e("callback", "Result is failure: ${result.exceptionOrNull()}")
            viewModel.updateShares(emptyList())
        }
        isLoading = false
    }


    LaunchedEffect(pageTab, saveLoading) {
        if (user != null) {
            if (!saveLoading!!) {
                isLoading = true // 开始加载
                when (pageTab) {
                    0 -> e.getMyselfShare(null, null, user.id, ::callback)
                    1 -> e.shareGetSave(null, null, user.id, ::callback)
                }
            }
        }
        isLoading = false // 确保在请求结束后设置为false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if(items.isNotEmpty()){
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 65.dp)
            ) {
                Log.e("items", "items: $items", )
                itemsIndexed(viewModel.shares.value ?: emptyList()) { index, item ->
                    MyShareCard(item = item, index = index)
                }
            }
        }else{
            Box(Modifier.fillMaxSize()){
                Image(painter = painterResource(id = R.drawable.empty), contentDescription ="空" ,Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MyShareCard(index:Int,item: Share) {
    val viewModel= LocalMyViewModel.current
    val context = LocalContext.current
    val isEdit by viewModel.isEdit.observeAsState()
    val tabList by viewModel.selectList.observeAsState()
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
        Box{
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
            if(isEdit==true){
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            viewModel.updateSelectList(index)
                        }
                        .background(if (tabList!![index]) Color.White else Color.Red)
                )
            }
        }
    }
}

@Composable
fun ShareCard1(item:Share,index:Int){
    Row(
        Modifier
            .background(Color.White)
            .height(100.dp)
    ) {
        AsyncImage(model = item.imageUrlList[0], contentDescription ="url" ,Modifier.fillMaxHeight())
    }
    HorizontalDivider(color = Color.LightGray,thickness=5.dp)
}

//@Preview(showBackground = true)
//@Composable
//fun MyScreen(){
////    val viewModel= LocalMyViewModel.current
//    val context= LocalContext.current
//    val user: User? = getFromPreferences<User>(context, "user")
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ){
//        AsyncImage(
//            model = user?.avatar ?: "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/12/14/b15f3e06-4175-494a-9a81-8ed0b47ae87b.png",
//            contentDescription ="url",
//            Modifier.fillMaxWidth()
//                .size(200.dp)
//        )
//    }
//}
//
////图片选择
//@Composable
//fun ImagePickerScreen() {
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//
//    // Create an ActivityResultLauncher for the image picker
//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        uri?.let {
//            imageUri = it
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Display the selected image or a placeholder
//        imageUri?.let {
//            Image(
//                painter = rememberAsyncImagePainter(
//                    ImageRequest.Builder(LocalContext.current).data(data = it).apply(block = fun ImageRequest.Builder.() {
//                        scale(Scale.FILL)
//                    }).build()
//                ),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(200.dp)
//                    .padding(16.dp)
//            )
//        } ?: run {
//            Text("No image selected", fontSize = 20.sp)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Button to launch the image picker
//        Button(onClick = { launcher.launch("image/*") }) {
//            Text("Select Image")
//        }
//    }
//}
////@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//        ImagePickerScreen()
//}
