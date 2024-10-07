package com.example.jixiv.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.jixiv.R
import com.example.jixiv.utils.getFromPreferences
import com.example.jixiv.viewModel.LoginViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LoginViewModel by viewModels()
            val context = LocalContext.current
            val checked = getFromPreferences<Boolean>(context, "checked") ?: false

            if (checked) {
                viewModel.updateChecked(true)
                getFromPreferences<String>(LocalContext.current,"username")?.let { viewModel.updateUsername(it) }
                getFromPreferences<String>(LocalContext.current,"password")?.let { viewModel.updatePassword(it) }
            }
            AppTheme {
                Login(context = context, viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JixivLogin() {
    AppTheme {
        Login(context = LocalContext.current, viewModel = LoginViewModel())
    }
}

@Composable
fun Login(context: Context,viewModel: LoginViewModel){
    val usernameText by viewModel.username.observeAsState("")
    val passwordText by viewModel.password.observeAsState("")
    val checked by viewModel.checked.observeAsState(false)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
    ){
        Image(painter = painterResource(id = R.drawable.name),
            contentDescription ="appName",
            modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
                Row (
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ){
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth()
                            .height(60.dp),
                        value = usernameText,
                        onValueChange = { viewModel.updateUsername(it)},
                        label = { Text(text ="账号")},
                        leadingIcon={
                            Icon(
                                modifier = Modifier.height(40.dp),
                                painter = painterResource(id = R.drawable.username),
                                contentDescription = "Username Icon",
                            )
                        }
                    )
                }

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth()
                            .height(60.dp),
                        value = usernameText,
                        onValueChange = { viewModel.updatePassword(it)},
                        label = { Text(text ="账号")},
                        leadingIcon={
                            Icon(
                                modifier = Modifier.height(40.dp),
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = "Username Icon",
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Spacer(modifier = Modifier.weight(1f))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { viewModel.updateChecked(!checked) }
                    )
                    Text(
                        "Remember Password"
                    )
                }
                Spacer(modifier = Modifier.weight(3f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                Modifier.padding(10.dp)
            ){
                Button(onClick = {
                    viewModel.login(context)
                                 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    ) {
                    Text(text = "登录")
                }
                Spacer(modifier =Modifier.heightIn(20.dp) )
                Button(onClick = {
                    Log.e("LoginActivity", "Register: ")
                    val intent = Intent(context, RegisterActivity::class.java)
                    context.startActivity(intent)
                                 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(50.dp)
                ) {
                    Text(text = "注册")
                }

            }
        }

    }
}
