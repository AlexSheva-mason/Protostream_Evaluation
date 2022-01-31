package alexei.sevcisen.android.protostreamevaluation

import alexei.sevcisen.android.protostreamevaluation.ui.theme.ProtostreamEvaluationTheme
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

const val LOGIN_SCREEN_ID = "login_screen"
const val MAIN_LIST_SCREEN_ID = "main_list_screen"
const val MOVIE_ID_SCREEN_ID = "movie_id_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProtostreamEvaluationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = LOGIN_SCREEN_ID) {
                    composable(LOGIN_SCREEN_ID) { LoginScreen(navController = navController) }
                    composable(MAIN_LIST_SCREEN_ID) { MainListScreen(navController = navController) }
                    composable("$MOVIE_ID_SCREEN_ID/{movieId}") { backStackEntry ->
                        MovieIdScreen(id = backStackEntry.arguments?.getString("movieId"))
                    }
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

@OptIn(ExperimentalFoundationApi::class)
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
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 6.dp,
                top = 12.dp,
                end = 6.dp,
                bottom = 12.dp
            )
        ) {
            movieData.forEach {
                item {
                    MovieCardExtendable(it, navController)
                }
            }
        }
    }
}

@Composable
fun MovieIdScreen(id: String?) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Movie ID: $id")
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MovieCardExtendable(movie: Movie, navController: NavController?) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        expanded = !expanded
                    },
                    onTap = {
                        navController?.navigate("$MOVIE_ID_SCREEN_ID/${movie.id}")
                    }
                )
            }
    ) {
        val thumbnailUrl = movie.images?.find { it.type == "thumbnail" }?.url
        val heroMobileUrl = movie.images?.find { it.type == "hero-mobile" }?.url
        val imageUrl = if (expanded) heroMobileUrl else thumbnailUrl
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberImagePainter(data = "$imageUrl",
                    builder = {
                        size(OriginalSize)
                    }),
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth(),
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
