package alexei.sevcisen.android.protostreamevaluation

import alexei.sevcisen.android.protostreamevaluation.ui.theme.ProtostreamEvaluationTheme
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

const val LOGIN_SCREEN_ID = "login_screen"
const val MAIN_LIST_SCREEN_ID = "main_list_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProtostreamEvaluationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = LOGIN_SCREEN_ID) {
                    composable(LOGIN_SCREEN_ID) { LoginScreen(navController = navController) }
                    composable(MAIN_LIST_SCREEN_ID) { MainListScreen(navController = navController) }
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
    val context = LocalContext.current
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
            onClick = {
                val credentialsValid = evaluateEmailAndPassword(email, password)
                if (!credentialsValid) {
                    Toast.makeText(context, "Email or password is invalid", Toast.LENGTH_LONG)
                        .show()
                } else {
                    navController?.navigate(MAIN_LIST_SCREEN_ID) {
                        popUpTo(LOGIN_SCREEN_ID) { inclusive = true }
                    }
                }
            }) {
            Text(text = "Sign in")
        }
    }
}

@Composable
fun MainListScreen(navController: NavController?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composableScope = rememberCoroutineScope()
        val context = LocalContext.current
        var movieData by remember { mutableStateOf(listOf<Movie>()) }
        composableScope.launch {
            try {
                val movies = RetrofitServiceProvider.apiService.getMovieData()
                movieData = movies
            } catch (ex: HttpException) {
                showErrorToast(ex, context)
            } catch (ex: IOException) {
                showErrorToast(ex, context)
            }
        }
        LazyColumn {
            movieData.forEach {
                item {
                    MovieCardExtendable(it)
                    Text(text = it.id)
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MovieCardExtendable(movie: Movie) {
    var expanded by remember { mutableStateOf(false) }
    val cardWidth = if (expanded) 200.dp else 150.dp
    val imageHeight = if (expanded) 120.dp else 90.dp
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .width(cardWidth)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        expanded = !expanded
                    }
                )
            }
    ) {
        val thumbnailUrl = movie.images?.find { it.type == "thumbnail" }?.url
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberImagePainter("$thumbnailUrl"),
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .width(cardWidth)
                    .height(imageHeight),
                contentDescription = null
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = movie.duration,
                style = MaterialTheme.typography.body2,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f))
                    .align(Alignment.End)
                    .padding(4.dp)
            )
            AnimatedVisibility(
                visible = expanded,
            ) {
                Divider()
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.caption,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
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

private fun evaluateEmailAndPassword(email: String, password: String): Boolean {
    if (email.isBlank() || password.isBlank()) {
        return false
    }
    return (email.contains('@') && email.contains('.') && email.length > 5)
}

private fun showErrorToast(exception: Exception, context: Context) {
    Toast.makeText(
        context,
        "Error occurred while fetching data from api: $exception",
        Toast.LENGTH_LONG
    ).show()
}
