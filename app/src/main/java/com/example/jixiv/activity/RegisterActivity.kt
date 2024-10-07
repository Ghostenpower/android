package com.example.jixiv.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.jixiv.R
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.PreferencesHelper
import com.example.jixiv.viewModel.LoginViewModel
import com.example.jixiv.viewModel.RegisterViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            JixivRegister()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun JixivRegister() {
    AppTheme {
        Register(context = LocalContext.current,registerViewModel = RegisterViewModel())
    }
}
@Composable
fun Register(context: Context,registerViewModel: RegisterViewModel){
    val preferencesHelper = remember { PreferencesHelper(context) }

    var usernameText by rememberSaveable { mutableStateOf(registerViewModel.username.value) }
    var passwordText by rememberSaveable { mutableStateOf(registerViewModel.password.value) }
    var repasswordText by rememberSaveable { mutableStateOf(registerViewModel.repassword.value) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
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
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(60.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.username), contentDescription = "username")
                    TextField(
                        value = usernameText,
                        onValueChange = { newValue ->
                            usernameText = newValue // Update the state
                            registerViewModel.updateUsername(newValue) // Update ViewModel if needed
                        },
                        label = { Text("账号") }
                    )
                }
//            HorizontalDivider(thickness = 2.dp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painter = painterResource(id = R.drawable.password),
                        contentDescription ="password",
                        modifier = Modifier.height(60.dp)
                    )
                    var showPassword by rememberSaveable { mutableStateOf(false) }
                    TextField(

                        value = passwordText,
                        onValueChange = {newValue->
                            passwordText=newValue
                            registerViewModel.updatePassword(newValue)
                                        },
                        label = { Text(text ="密码") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
//            HorizontalDivider(thickness = 2.dp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painter = painterResource(id = R.drawable.password),
                        contentDescription ="password",
                        modifier = Modifier.height(60.dp)
                    )
                    var showRepassword by rememberSaveable { mutableStateOf(false) }
                    TextField(
                        value = repasswordText,
                        onValueChange = {newValue->
                            repasswordText=newValue
                            registerViewModel.updateRePassword(newValue)
                                        },
                        label = { Text(text ="重复密码") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
//            HorizontalDivider(thickness = 2.dp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row {
                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = {
                    (context as Activity).finish()
                },
                    modifier = Modifier
                        .weight(4f)
                        .aspectRatio(2f / 1f)
                ) {
                    Text(text = "返回登录")
                }

                Spacer(modifier = Modifier.weight(2f))

                Button(onClick = {
                    Log.e("password", "$usernameText $repasswordText", )
                    if(passwordText==repasswordText && usernameText!=""){
                        Log.e("Register", "Register: ", )
                        val e = ExampleRepository()
                        e.register(usernameText, passwordText) { result ->
//                            Log.e("Register", "Register: ${result}", )
                            val data=result.getOrNull()
                            if(data==200) {
                                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show()
                                (context as Activity).finish()
                            }
                        }
                    }
                    else{
                        if(usernameText==""){
                            Toast.makeText(context, "用户名不能为空", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context, "密码不一致", Toast.LENGTH_SHORT).show()
                        }
                    }
                                 },
                    modifier = Modifier
                        .weight(4f)
                        .aspectRatio(2f / 1f)
                ) {
                    Text(text = "确认注册")
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

    }
}
