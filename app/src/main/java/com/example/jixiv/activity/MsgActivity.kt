package com.example.jixiv.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.example.jixiv.R
import com.example.jixiv.components.AvatarWithFrame
import com.example.jixiv.utils.User
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.viewModel.MsgViewModel
import com.example.jixiv.viewModel.ShareViewModel
import kotlinx.coroutines.launch

class MsgActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val viewModel=MsgViewModel(1)
                MsgScreen(viewModel)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun MsgScreen(){
    AppTheme {
        MsgScreen(viewModel = MsgViewModel(1))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MsgScreen(viewModel: MsgViewModel){
    val context= LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    viewModel.updateUser(context)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {  },
                navigationIcon={
                    IconButton(onClick = {
                        (context as Activity).finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription ="back", tint = Color.White )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.editUserMsg(context)
                        }
                    }) {
                        Icon(Icons.Filled.Check, contentDescription ="back", tint = Color.White )
                    }
                }
            )
        },
    ){ innerPadding ->
//        var showDialog by remember {
//            mutableStateOf(false)}
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color.White)
//        ) {
//            NavHost(navController =navController ,startDestination="Edit" ){
//                composable(
//                    "Msg",
//                    enterTransition = { fadeIn(animationSpec = tween(300)) },
//                    exitTransition = { fadeOut(animationSpec = tween(300)) },
//                ) {
////                    MsgMain()
//                }
//                composable(
//                    "Eidt",
//                    enterTransition = { slideInHorizontally(animationSpec = tween(300)) { it } },
//                    exitTransition = { slideOutHorizontally(animationSpec = tween(300)) { it } }
//                ) {
//                    EditMain(viewModel)
//                }
//            }
//        }
        Box(Modifier.padding(innerPadding)){
            Box(modifier = Modifier.padding(15.dp)){
                EditMain(viewModel)
            }
        }
    }
}

//@Composable
//fun MsgMain() {
//    val context= LocalContext.current
//    val message= getFromPreferences<User>(context,"message")
//    Column {
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .height(150.dp)
//            .padding(bottom = 10.dp)){
//            Box(modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)
//                .background(colorResource(id = R.color.blue)) )
//            Box(modifier = Modifier.align(Alignment.BottomCenter)){
//                AvatarWithFrame(url =message!!.avatar!! , size = 100.dp)
//            }
//        }
//        Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
//            Button(onClick = { /*TODO*/ }) {
//                Text(text = "关注")
//            }
//        }
//        Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
//            Text(text = message!!.username)
//        }
//        Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
//            Text(text = message!!.introduce!!)
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMain(viewModel: MsgViewModel){
    val user by viewModel.user.observeAsState()
    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.addUriToUriList(it)
            scope.launch {
                viewModel.imageUpload(context)
                viewModel.editUserMsg(context)
            }
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.Start,
    ) {
        item{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                ){
                Box(Modifier.background(Color.Transparent)){
                    AvatarWithFrame(url = (user!!.avatar?:"null").toString(), size =80.dp )
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .clickable {

                            },
                        shape = CircleShape,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(), // 填满 Card
                            contentAlignment = Alignment.Center // Center the icon
                        ) {
                            IconButton(onClick = {
                                scope.launch {
                                    launcher.launch("image/*")
                                }
                            }) {
                                Icon(
                                    contentDescription = null,
                                    imageVector = Icons.Default.Edit,
                                    modifier = Modifier.size(20.dp) // 可根据需要调整图标大小
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Divider(color = Color.LightGray,thickness = 1.dp)
            Text(text = "昵称", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Color.LightGray.copy(alpha = 0.5f))
            TextField(
                value = user!!.username,
                onValueChange = {
                    viewModel.updateUserName(it)
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.blue),
                    containerColor = Color.Transparent
                ),
                textStyle=androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Divider(color = Color.LightGray,thickness =1.dp)
            Text(text = "性别", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Color.LightGray.copy(alpha = 0.5f))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

                ){
                Text(text = if(user!!.sex==0)"男" else "女", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                SelectTypeView(viewModel)
            }
        }
        item {
            Divider(color = Color.LightGray,thickness = 1.dp)
            Text(text = "简介", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Color.LightGray.copy(alpha = 0.5f))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = user!!.introduce?:"",
                onValueChange = {viewModel.updatecontent(it)},
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.blue),
                    containerColor = Color.LightGray.copy(alpha = 0.3f)
                ),
            )
        }
    }
}

@Composable
fun SelectTypeView(viewModel: MsgViewModel) {
    val typeList = mutableListOf(
        "男",
        "女",
    )
    val isClick = rememberSaveable { mutableStateOf(false) }
    val user by viewModel.user.observeAsState()
    Column(
    ) {
        Button(
            onClick = { isClick.value = !isClick.value },
            content = {
                Text(text = if(user!!.sex==0)"男" else "女")
            },
        )
        DropdownMenu(
            expanded = isClick.value,
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {},
            content = {
                typeList.forEach {
                    DropdownMenuItem(
                        onClick = {
                            if(it=="男"){
                                viewModel.updateSex(0)
                            }else viewModel.updateSex(1)
                            isClick.value = !isClick.value
                        },
                        content = {
                            Text(text = it)
                        }
                    )
                }
            },
        )
    }
}