package com.example.securesocialapp.ui.screen.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.securesocialapp.ui.screen.PostTag
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.PostViewModel
import androidx.compose.material3.OutlinedTextField
import com.example.securesocialapp.ui.navigation.PostsScreenNav

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavHostController,
    postViewModel: PostViewModel
) {
    // Local Form State
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<PostTag?>(null) }

    // ViewModel State
    val uiState = postViewModel.createPostUiState

    // Navigation Listener
    LaunchedEffect(uiState) {
        if (uiState is BaseUiState.Success && uiState.data != null) {
            postViewModel.resetCreatePostState()
            navController.navigate(PostsScreenNav)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Create Post", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (selectedTag != null) {
                                postViewModel.createPost(title, content, selectedTag!!)
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank() && selectedTag != null && uiState !is BaseUiState.Loading
                    ) {
                        if (uiState is BaseUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Post", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 1. Tag Selector
            Text(
                text = "Select a Tag",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TagSelector(
                selectedTag = selectedTag,
                onTagSelected = { selectedTag = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("Give your post a headline") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor =  MaterialTheme.colorScheme.surfaceContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFEEEEEE)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            // 3. Content Input (Expands)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Taller box
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor =  MaterialTheme.colorScheme.surfaceContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFEEEEEE)
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            // Error Message (if any)
            if (uiState is BaseUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Failed to create post. Please try again.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun TagSelector(
    selectedTag: PostTag?,
    onTagSelected: (PostTag) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp) // Slight padding to avoid clipping shadows
    ) {
        items(PostTag.entries.toTypedArray()) { tag ->
            val isSelected = selectedTag == tag

            // Animate color change
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5),
                label = "bgColor"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Black,
                label = "textColor"
            )

            // Custom "Cool" Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50)) // Pill shape
                    .background(backgroundColor)
                    .clickable { onTagSelected(tag) }
                    .padding(horizontal = 16.dp, vertical = 10.dp), // Generous padding for touch
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = tag.name,
                        color = contentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}