package com.example.securesocialapp.ui.screen.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.securesocial.data.model.response.PostLikesResponse
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.PostViewModel
import com.example.securesocialapp.ui.screen.common.formatConciseTime
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostLikesDialog(
    postId: String,
    onDismiss: () -> Unit,
    postViewModel: PostViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(postId) {
        postViewModel.getPostLikes(postId)
    }

    val uiState = postViewModel.postLikesUiState

    Dialog(onDismissRequest = onDismiss) {
        PostLikesDialogContent(
            uiState = uiState,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostLikesDialogContent(
    uiState: BaseUiState<List<PostLikesResponse>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Liked by",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider(color = Color(0xFFD7D7D7))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(top = 8.dp, bottom = 28.dp, start = 8.dp, end = 8.dp)
            ) {
                when (uiState) {
                    is BaseUiState.Loading -> Box(
                        modifier = Modifier.height(150.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is BaseUiState.Error -> Box(
                        modifier = Modifier.height(150.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Failed to load likes", color = Color.Red)
                    }

                    is BaseUiState.Success -> {
                        if (uiState.data.isEmpty()) {
                            Box(
                                modifier = Modifier.height(100.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No likes yet", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(top = 16.dp)
                            ) {
                                items(uiState.data) { like ->
                                    LikeItem(like)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikeItem(like: PostLikesResponse) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar Placeholder
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = like.username.first().uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = like.username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatConciseTime(like.likedAt),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

// Preview remains ensuring it matches the actual UI
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PostLikesDialogPreview() {
    MaterialTheme {
        Surface {
            PostLikesDialogContent(
                uiState = BaseUiState.Success(
                    listOf(
                        PostLikesResponse(
                            username = "john_doe",
                            postId = "1",
                            likedAt = java.time.Instant.now().toEpochMilli()
                        ),
                        PostLikesResponse(
                            username = "jane_smith",
                            postId = "1",
                            likedAt = java.time.Instant.now().minusSeconds(3600).toEpochMilli()
                        )
                    )
                ),
                onDismiss = {}
            )
        }
    }
}