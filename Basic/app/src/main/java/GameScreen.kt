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
import kotlin.OptIn
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController) {
    // Core state
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }   // change to 60 if you want
    var moleIndex by remember { mutableIntStateOf(0) }   // 0..8
    var isRunning by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }

    // High score
    val context = LocalContext.current
    val prefs = remember { HighScorePrefs(context) }
    var highScore by remember { mutableIntStateOf(prefs.getHighScore()) }

    fun startOrRestart() {
        score = 0
        timeLeft = 30
        moleIndex = Random.nextInt(9)
        showGameOver = false
        isRunning = true
    }

    fun onHoleClick(index: Int) {
        if (isRunning && index == moleIndex) {
            score += 1
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

            // 3x3 grid UI (mole displayed in exactly one spot)
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
                        modifier = Modifier.height(90.dp)
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
