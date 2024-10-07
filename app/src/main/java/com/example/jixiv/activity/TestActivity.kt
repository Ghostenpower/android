package com.example.jixiv.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.colorspace.Illuminant.A
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.jixiv.viewModel.MyViewModel
import com.example.jixiv.viewModel.TestViewModel
import kotlinx.coroutines.launch

class TestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel=TestViewModel()
            MyApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel=TestViewModel()
    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.addShare(context)
        }
    }
    Box {
        NavHost(navController, startDestination = "home") {
            composable(
                "home",
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
            ) { TestScreen(navController,viewModel) }
            composable(
                "details/{item}",
                arguments = listOf(navArgument("item") { type = NavType.IntType }),
                enterTransition = { slideInHorizontally(animationSpec = tween(300)) { it } },
                exitTransition = { slideOutHorizontally(animationSpec = tween(300)) { it } }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item") ?: return@composable
                DetailScreen(id, navController,viewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(navController: NavController,viewModel: TestViewModel) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Home") })
                Button(onClick = { navController.navigate("details/1") }) { // 传递 item 参数
                    Text("Go to Details")
                }
                Button(onClick = { navController.navigate("details/2") }) { // 传递 item 参数
                    Text("Go to Details")
                }
            }
        }
    ) {
        Column {
            Text("Welcome to the Home Screen")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(itemId: Int, navController: NavController,viewModel: TestViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Details") })
        }
    ) {innerPadding->
        Box(Modifier.padding(top = innerPadding.calculateTopPadding())){
            Text("This is the detail screen for item $itemId") // 显示 itemId
        }
    }
}
