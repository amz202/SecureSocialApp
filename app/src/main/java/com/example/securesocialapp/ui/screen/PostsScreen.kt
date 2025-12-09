package com.example.securesocialapp.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.ui.navigation.navbar.bottomNavItems
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.PostViewModel
import com.example.securesocialapp.ui.viewModel.PostsUiState
import kotlin.toString

enum class PostTag {
    SECURITY, TECH, NEWS, LIFESTYLE, MEME, RANDOM
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostsScreen(
    postsUiState: PostsUiState,
    navController: NavHostController,
    postViewModel: PostViewModel = viewModel(factory = PostViewModel.postFactory),
) {
    LaunchedEffect(Unit) {
        postViewModel.getAllPosts()
    }

    when (postsUiState) {
        is BaseUiState.Success -> PostsList(
            posts = postsUiState.data,
            navController = navController,
            postViewModel = postViewModel,
        )
        is BaseUiState.Loading -> LoadingScreen()
        is BaseUiState.Error -> ErrorScreen(
            onRetry = { postViewModel.getAllPosts() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostsList(
    modifier: Modifier = Modifier,
    posts: List<PostResponse>,
    navController: NavHostController,
    postViewModel: PostViewModel,
) {
    val selectedTag = postViewModel.currentTag
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val items = bottomNavItems
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Posts",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTag == null,
                        onClick = {
                            postViewModel.toggleTag(null)
                        },
                        label = { Text("All") }
                    )
                }
                items(PostTag.entries.toTypedArray()) { tag ->
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = {
                            postViewModel.toggleTag(tag)
                        },
                        label = { Text(tag.name) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        modifier = Modifier.padding(bottom = 16.dp),
                        onClick = { /* Navigate to post detail */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: PostResponse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface, // Ensure distinct background
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header Row: Title and Tag.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                // Custom sleek tag badge instead of the bulky AssistChip
                Surface(
                    shape = RoundedCornerShape(50), // pill shape
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = post.tag.uppercase(), // Uppercase often looks tidier for short tags
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content text: Use onSurfaceVariant for a slightly softer text color relative to the title
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row: Stats only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End, // Push stats to the end
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    val likeColor = Color(0xFFE91E63) // A nice Material Pink/Red color
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Likes",
                        modifier = Modifier.size(18.dp), // Slightly larger icon
                        tint = likeColor
                    )
                    Text(
                        text = post.likeCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = likeColor // Match text color to icon
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Views
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = post.viewCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PostItemPreview() {
    MaterialTheme {
        PostItem(
            post = PostResponse(
                id = "1",
                title = "Sample Post Title",
                content = "This is a sample post content that demonstrates how the post item looks in the UI. It can be longer to show how text wraps.",
                tag = "TECH",
                createdAt = System.currentTimeMillis(),
                authorId = "user123",
                likeCount = 42,
                viewCount = 156
            ),
            modifier = Modifier.padding(16.dp),
            onClick = { }
        )
    }
}