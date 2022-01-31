package alexei.sevcisen.android.protostreamevaluation

import alexei.sevcisen.android.protostreamevaluation.ui.theme.ProtostreamEvaluationTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
                NavHost(navController = navController, startDestination = "profile") {
                    composable("login_screen") { LoginScreen(navController = navController) }
                    composable("main_list_screen") { MainListScreen(navController = navController) }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController?) {

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
