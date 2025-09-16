package com.example.pathfinder.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pathfinder.model.ChatMessage
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.AdvisorViewModel
import com.example.pathfinder.viewmodel.AdvisorViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AdvisorScreen() { // <-- The parameter has been removed
    // Get the application context, which is needed by the factory
    val context = LocalContext.current.applicationContext

    // Create the ViewModel using the factory. This is the only declaration now.
    val advisorViewModel: AdvisorViewModel = viewModel(
        factory = AdvisorViewModelFactory(context as Application)
    )

    val messages by advisorViewModel.messages.collectAsState()
    val isLoading by advisorViewModel.isLoading.collectAsState()

    // Call the stateless composable that displays the UI
    AdvisorScreenContent(
        messages = messages,
        isLoading = isLoading,
        onSendMessage = { advisorViewModel.sendMessage(it) }
    )
}

    /**
     * This is a stateless version of the screen that only displays the UI.
     * It's easy to preview and test because it doesn't depend on a ViewModel.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdvisorScreenContent(
        messages: List<ChatMessage>,
        isLoading: Boolean,
        onSendMessage: (String) -> Unit
    ) {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        // Automatically scroll to the bottom when a new message arrives
        LaunchedEffect(messages.size) {
            coroutineScope.launch {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Career Advisor") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                MessageInput(
                    onSendMessage = onSendMessage,
                    isLoading = isLoading
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }

@Composable
fun MessageBubble(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isFromUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Text(
            text = message.message,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
                .widthIn(max = 300.dp),
            color = textColor
        )
    }
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit, isLoading: Boolean) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about your career...") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions {
                    onSendMessage(text)
                    text = ""
                    keyboardController?.hide()
                },
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            } else {
                IconButton(
                    onClick = {
                        onSendMessage(text)
                        text = ""
                        keyboardController?.hide()
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * The preview now calls the stateless AdvisorScreenContent with fake data.
 * This will no longer crash and allows you to build your UI in isolation.
 */
@Preview(showBackground = true)
@Composable
fun AdvisorScreenPreview() {
    PathfinderAITheme {
        val previewMessages = listOf(
            ChatMessage("Hello! How can I help you today?", isFromUser = false),
            ChatMessage("I want to know about a career in software engineering.", isFromUser = true),
            ChatMessage("Of course! Software engineering is a vast field...", isFromUser = false)
        )
        AdvisorScreenContent(
            messages = previewMessages,
            isLoading = false,
            onSendMessage = {}
        )
    }
}