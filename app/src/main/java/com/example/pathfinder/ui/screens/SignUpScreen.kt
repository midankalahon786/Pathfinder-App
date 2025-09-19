package com.example.pathfinder.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.network.data.TokenManager
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.AuthViewModel
import com.example.pathfinder.viewmodel.AuthState

// Assuming Teal500 is defined in your theme
val Teal500 = Color(0xFF009688) // Example color

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Pass in the ViewModel
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // --- Additions for ViewModel Integration ---
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                tokenManager.saveToken(state.data.token)
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                onSignUpSuccess()
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> Unit
        }
    }
    // --- End of Additions ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPurpleBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ... (Image and Text views remain the same)
            Image(
                painter = painterResource(id = R.drawable.pathfinderailogo),
                contentDescription = "Pathfinder AI Logo",
                modifier = Modifier.size(120.dp)
            )
            // ...

            Spacer(modifier = Modifier.height(48.dp))

            val areFieldsEnabled = authState != AuthState.Loading

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                // ... (other modifiers)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                // ... (other modifiers)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                // ... (other modifiers)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                // ... (other modifiers)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Logic for Button and Loading Indicator ---
            if (authState == AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        // Client-side validation
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        authViewModel.register(email, password, name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                ) {
                    Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            // --- End of Logic ---


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Teal500,
                    modifier = Modifier.clickable { if (areFieldsEnabled) navController.popBackStack() }
                )
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    PathfinderAITheme {
        val fakeNavController: NavController = rememberNavController()
        val fakeAuthViewModel = AuthViewModel()
        SignUpScreen(rememberNavController(), onSignUpSuccess = {}, authViewModel = fakeAuthViewModel)
    }
}