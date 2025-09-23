package com.example.pathfinder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.network.data.TokenManager
import com.example.pathfinder.ui.screens.fake.FakeAuthViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.AuthState
import com.example.pathfinder.viewmodel.IAuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: IAuthViewModel,
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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

    // THEME: Use Surface with theme background color
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pathfinderailogo),
                contentDescription = "Pathfinder AI Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            val areFieldsEnabled = authState != AuthState.Loading

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) painterResource(R.drawable.baseline_visibility_24) else painterResource(R.drawable.baseline_visibility_off_24)
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = description)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = areFieldsEnabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) painterResource(R.drawable.baseline_visibility_24) else painterResource(R.drawable.baseline_visibility_off_24)
                    val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(painter = image, contentDescription = description)
                    }
                }
            )

            if (confirmPassword.isNotEmpty()) {
                val (message, color) = if (password == confirmPassword) {
                    // THEME: Use a theme accent color (tertiary) for success messages
                    "Passwords match" to MaterialTheme.colorScheme.tertiary
                } else {
                    "Passwords do not match" to MaterialTheme.colorScheme.error
                }
                Text(
                    text = message,
                    color = color,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 4.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (authState == AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
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
                    // THEME: Use primary theme color for the main button
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }

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
                    // THEME: Use primary theme color for links
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { if (areFieldsEnabled) navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    PathfinderAITheme {
        val fakeNavController = rememberNavController()
        SignUpScreen(
            navController = fakeNavController,
            onSignUpSuccess = {},
            authViewModel = FakeAuthViewModel()
        )
    }
}