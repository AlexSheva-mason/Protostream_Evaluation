package alexei.sevcisen.android.protostreamevaluation

import alexei.sevcisen.android.protostreamevaluation.ui.theme.ProtostreamEvaluationTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProtostreamEvaluationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login_screen") {
                    composable("login_screen") { LoginScreen(navController = navController) }
                    composable("main_list_screen") { MainListScreen(navController = navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            modifier = Modifier.padding(10.dp),
            label = { Text("e-mail") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
            ),
            onValueChange = { email = it })
        OutlinedTextField(
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(10.dp),
            label = { Text("password") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            onValueChange = { password = it })
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = { /*TODO*/ }) {
            Text(text = "Login")
        }
    }
}

@Composable
fun MainListScreen(navController: NavController?) {

}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL
)
@Composable
fun LoginScreenPreview() {
    ProtostreamEvaluationTheme {
        LoginScreen(null)
    }
}
