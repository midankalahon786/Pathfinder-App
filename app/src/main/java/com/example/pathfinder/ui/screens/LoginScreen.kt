package com.example.pathfinder.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.network.data.TokenManager
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.AuthViewModel
import com.example.pathfinder.viewmodel.AuthState

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Pass in the ViewModel
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- Additions for ViewModel Integration ---
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Handle side effects like navigation or showing toasts
    LaunchedEffect(key1 = authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                tokenManager.saveToken(state.data.token)
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
                authViewModel.resetState() // Reset state after handling
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState() // Reset state after showing error
            }
            else -> Unit // Do nothing for Idle or Loading
        }
    }
    // --- End of Additions ---

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(id = R.drawable.pathfinderailogo_withoutbg),
                contentDescription = "App Icon",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Personal Career Mentor",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = authState != AuthState.Loading // Disable field when loading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = authState != AuthState.Loading // Disable field when loading
                )
                Spacer(modifier = Modifier.height(32.dp))

                // --- Logic for Button and Loading Indicator ---
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            // Basic validation
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.login(email, password)
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealHeader,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Login")
                    }
                }
                // --- End of Logic ---
            }

            TextButton(onClick = { navController.navigate("signup") }) { // Assuming "signup" is your route
                Text("Don't have an account? Sign Up", color = TealHeader)
            }
        }
    }
}

// Preview still works, but without ViewModel logic
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PathfinderAITheme {
        val fakeNavController: NavController = rememberNavController()
        val fakeAuthViewModel = AuthViewModel()
        LoginScreen(fakeNavController, onLoginSuccess = {}, authViewModel = fakeAuthViewModel)
    }
}