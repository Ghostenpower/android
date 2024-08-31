package com.example.jixiv.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.jixiv.R
import com.example.jixiv.viewModel.ShareDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputKeyboard(viewModel: ShareDetailViewModel){
    val coroutineScope = rememberCoroutineScope()
    val content by viewModel.content.observeAsState()
    val checkedFComment by viewModel.checkedFComment.observeAsState()
    val context= LocalContext.current
    val isVisible by viewModel.isVisible.observeAsState()

    if(isVisible==true){
        val focusRequester = remember { FocusRequester() }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f))
                .clickable {
                    viewModel.updateIsVisible()
                    viewModel.updatecheckedFComment()
                }
        ){
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(10.dp)
            ){
                Row (
                    Modifier.background(Color.LightGray.copy(alpha = 0.5f)),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TextField(
                        value = content!!,
                        onValueChange = { viewModel.updateContent(it) },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = colorResource(id = R.color.blue),
                            containerColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .heightIn(max = 200.dp)
                            .weight(3f)
                    )
                    IconButton(
                        onClick = { coroutineScope.launch {
                            viewModel.addComment(context =context)
                            Log.e("CustomInputKeyboard", "CustomInputKeyboard: ${checkedFComment!!.commentLevel}")
                            if(checkedFComment!!.commentLevel==0){
                                viewModel.getFirstComments()
                            }else{
                                viewModel.getSecondComments(checkedFComment!!)
                            }
                        } },
                        Modifier
                            .weight(1f)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}