package com.example.securesocialapp.ui.screen.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailScreen(
    postId: String,
    navController: NavHostController,
    postViewModel: PostViewModel = viewModel(factory = PostViewModel.postFactory)
) {
    LaunchedEffect(postId) {
        postViewModel.getPost(postId)
    }

    val uiState = postViewModel.postUiState

    Scaffold(
        containerColor = Color.White,
        topBar = {
            PostDetailTopBar(onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            when (uiState) {
                is BaseUiState.Loading -> LoadingScreen()
                is BaseUiState.Error -> ErrorScreen(onRetry = { postViewModel.getPost(postId) })
                is BaseUiState.Success -> PostDetailContent(
                    post = postViewModel.post.collectAsState().value,
                    onLikeClick = { postViewModel.likePost(postId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Post Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun PostDetailContent(
    post: PostResponse?,
    onLikeClick: () -> Unit
) {
    if(post==null)return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = post.tag.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Date
            Text(
                text = formatPostDate(post.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium, // Bigger, bolder title
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF0F0F0),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(4.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Author : ${post.authorName}",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 28.sp, // Good reading height
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = Color(0xFFEEEEEE))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like Button (Interactive)
            EngagementPill(
                icon = Icons.Default.Favorite, // Use FavoriteBorder if not liked yet (needs logic)
                count = post.likeCount.toInt(),
                color = Color(0xFFE91E63), // Pink
                onClick = onLikeClick
            )

            // View Counter (Static)
            EngagementPill(
                icon = Icons.Default.Visibility,
                count = post.viewCount.toInt(),
                color = Color.Gray,
                onClick = {} // No action for views
            )
        }
    }
}

@Composable
fun EngagementPill(
    icon: ImageVector,
    count: Int,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f), // Very light background of the same tint
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

// Helper for date formatting
private fun formatPostDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown Date"
    }
}