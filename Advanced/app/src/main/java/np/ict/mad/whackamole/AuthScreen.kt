package np.ict.mad.whackamole

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import np.ict.mad.whackamole.advanced.db.AppDatabase
import np.ict.mad.whackamole.advanced.db.UserEntity

@Composable
fun AuthScreen(
    db: AppDatabase,
    onAuthed: (UserEntity) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    fun signIn() {
        message = null
        CoroutineScope(Dispatchers.IO).launch {
            val user = db.userDao().signIn(username.trim(), password)
            withContext(Dispatchers.Main) {
                if (user != null) onAuthed(user)
                else message = "Invalid username or password."
            }
        }
    }

    fun signUp() {
        message = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newId = db.userDao().insert(
                    UserEntity(username = username.trim(), password = password)
                )
                val created = UserEntity(userId = newId, username = username.trim(), password = password)
                withContext(Dispatchers.Main) { onAuthed(created) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    message = "Username already exists (or sign-up failed)."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Sign In / Sign Up")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { signIn() },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank()
        ) {
            Text("Sign In")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { signUp() },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank()
        ) {
            Text("Sign Up")
        }

        if (message != null) {
            Spacer(Modifier.height(12.dp))
            Text(message!!)
        }
    }
}
