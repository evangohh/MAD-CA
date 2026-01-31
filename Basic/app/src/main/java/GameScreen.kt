package np.ict.mad.whackamole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.OptIn
import kotlin.random.Random
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController) {
    val gameDurationSeconds = 30

    // Core state
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(gameDurationSeconds) }
    var moleIndex by remember { mutableIntStateOf(0) } // 0..8
    var isRunning by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }

    // Used to force restart of LaunchedEffect loops even if isRunning stays true
    var gameId by remember { mutableIntStateOf(0) }

    // High score
    val context = LocalContext.current
    val prefs = remember { HighScorePrefs(context) }
    var highScore by remember { mutableIntStateOf(prefs.getHighScore()) }

    fun endGame() {
        isRunning = false
        showGameOver = true

        if (score > highScore) {
            highScore = score
            prefs.setHighScore(score)
        }
    }

    fun startOrRestart() {
        score = 0
        timeLeft = gameDurationSeconds
        moleIndex = Random.nextInt(9)
        showGameOver = false
        isRunning = true
        gameId += 1
    }

    fun onHoleClick(index: Int) {
        if (isRunning && timeLeft > 0 && index == moleIndex) {
            score += 1
        }
    }

    // Timer loop (every 1 second)
    LaunchedEffect(isRunning, gameId) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }

        if (isRunning && timeLeft <= 0) {
            endGame()
        }
    }

    // Mole movement loop (every 700–1000ms)
    LaunchedEffect(isRunning, gameId) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning && timeLeft > 0) {
            val interval = Random.nextLong(700, 1001) // 700..1000 inclusive
            delay(interval)
            moleIndex = Random.nextInt(9)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wack-a-Mole") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("High Score: $highScore")
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: $score")
                Text("Time: $timeLeft")
            }

            Spacer(Modifier.height(16.dp))

            // 3x3 grid (exactly one mole at any time)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((0..8).toList()) { index ->
                    val isMole = index == moleIndex
                    Button(
                        onClick = { onHoleClick(index) },
                        modifier = Modifier.height(90.dp),
                        enabled = isRunning && timeLeft > 0
                    ) {
                        Text(if (isMole) "🐹" else "")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = { startOrRestart() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isRunning) "Restart" else "Start")
            }

            if (showGameOver) {
                Spacer(Modifier.height(12.dp))
                Text("Game Over! Final score: $score")
            }
        }
    }
}
