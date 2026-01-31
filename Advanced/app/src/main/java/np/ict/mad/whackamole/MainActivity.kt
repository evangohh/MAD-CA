package np.ict.mad.whackamole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import np.ict.mad.whackamole.ui.theme.WhackAMoleTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import np.ict.mad.whackamole.advanced.db.AppDatabase
import np.ict.mad.whackamole.advanced.db.UserEntity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhackAMoleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhackAMoleTheme {
        Greeting("Android")
    }
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }

    var currentUser by remember { mutableStateOf<UserEntity?>(null) }

    val start = if (currentUser == null) "auth" else "game"

    NavHost(navController = navController, startDestination = start) {
        composable("auth") {
            AuthScreen(
                db = db,
                onAuthed = { user ->
                    currentUser = user
                    navController.navigate("game") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("game") {
            // IMPORTANT: pass currentUser + db so you can insert ScoreEntity at game end (Step 6)
            GameScreenAdvanced(
                navController = navController,
                db = db,
                currentUser = currentUser
            )
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("game") {
            GameScreenAdvanced(navController = navController, db = db, currentUser = currentUser)
        }
        composable("leaderboard") {
            LeaderboardScreen(navController = navController, db = db, currentUser = currentUser)
        }


    }

}