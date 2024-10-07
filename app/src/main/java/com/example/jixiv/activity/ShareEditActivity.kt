package com.example.jixiv.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.jixiv.R
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.viewModel.EditViewModel
import com.example.jixiv.viewModel.EditViewModelFactory
import kotlinx.coroutines.launch

val LocalEditViewModel = compositionLocalOf<EditViewModel> {
    error("No EditViewModel provided")
}
class EditActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 Jetpack Compose 设置 UI
        setContent {
            val context = LocalContext.current
            val viewModel: EditViewModel by viewModels {
                EditViewModelFactory()
            }

            // 从共享偏好中获取 Share 对象
            val share: Share? = getFromPreferences(context, "share")
            share?.let {
                viewModel.updateShare(it)
            }

            // 使用 CompositionLocalProvider 提供 ViewModel
            CompositionLocalProvider(LocalEditViewModel provides viewModel) {
                // 这里是你应用的其余部分
                EditScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(){
    val context= LocalContext.current
    val viewModel= LocalEditViewModel.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {  },
                navigationIcon={
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription ="back", tint = Color.White )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch{
                            viewModel.imageUpload(context)
                            viewModel.updateIsSave(true)
                            viewModel.addShare(context)
                        }
                    }) {
                        Text(text = "保存")
                    }
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.imageUpload(context)
                            viewModel.updateIsSave(false)
                            viewModel.addShare(context)
                        }
                    }) {
                        Text(text = "发布")
                    }
                }
            )
        },
    ){ innerPadding ->
        // Main content goes here
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            EditMain()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMain(){
    val context= LocalContext.current
    val viewModel= LocalEditViewModel.current
    val share by viewModel.share.observeAsState()
    val imgUriList by viewModel.imgUriList.observeAsState(emptyList())
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.addUriToUriList(it)
        }
    }

    Column (
        Modifier.background(Color.White)
    ){
        LazyRow (
            Modifier
                .height(150.dp)
                .padding(20.dp)
        ){
            itemsIndexed(imgUriList){idx,uri->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(5.dp)
                        )
                ){
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Red)
                        .clickable {
                            viewModel.deleteUriFromList(idx)
                        },
                    ){
                        Icon(imageVector = Icons.Filled.Close,
                            contentDescription ="delete" ,
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

            }
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clickable {
                            launcher.launch("image/*")
                        },
                ){
                    Icon(imageVector = Icons.Filled.AddPhotoAlternate, contentDescription ="addPhoto" ,Modifier.align(Alignment.Center))
                }
            }
        }
        Column {
            TextField(
                value = share!!.title,
                onValueChange = { newText ->
                    viewModel.upDateShareTitle(newText)
                },
                placeholder = {
                    Text(text = "填写标题", style = TextStyle(color = Color.LightGray))
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.blue),
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(0.dp, Color.Transparent)
            )
        }
        HorizontalDivider(color = Color.LightGray, thickness = 0.3.dp)
        Column {
            TextField(
                value = share!!.content,
                onValueChange = { newText ->
                    viewModel.upDateShareContent(newText)
                },
                placeholder = {
                    Text(text = "填写内容", style = TextStyle(color = Color.LightGray))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.blue),
                    containerColor = Color.White
                ),
            )
        }
    }
}