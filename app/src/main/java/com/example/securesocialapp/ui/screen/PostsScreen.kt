package com.example.securesocialapp.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.ui.navigation.ActivityLogScreenNav
import com.example.securesocialapp.ui.navigation.CreatePostScreenNav
import com.example.securesocialapp.ui.navigation.LoginScreenNav
import com.example.securesocialapp.ui.navigation.MyPostsScreenNav
import com.example.securesocialapp.ui.navigation.PostDetailsScreenNav
import com.example.securesocialapp.ui.navigation.PostsScreenNav
import com.example.securesocialapp.ui.navigation.navbar.bottomNavItems
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.screen.common.formatConciseTime
import com.example.securesocialapp.ui.viewModel.AuthViewModel
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.NavigationViewModel
import com.example.securesocialapp.ui.viewModel.PostViewModel
import com.example.securesocialapp.ui.viewModel.PostsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.toString

enum class PostTag {
    SECURITY, TECH, NEWS, LIFESTYLE, MEME, RANDOM
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostsScreen(
    postsUiState: PostsUiState,
    navController: NavHostController,
    postViewModel: PostViewModel,
    navigationViewModel: NavigationViewModel,
    userPreferences: UserPreferences,
    authViewModel: AuthViewModel
) {
    LaunchedEffect(Unit) {
        postViewModel.getAllPosts()
    }

    when (postsUiState) {
        is BaseUiState.Success -> PostsList(
            posts = postsUiState.data,
            navController = navController,
            postViewModel = postViewModel,
            navigationViewModel = navigationViewModel,
            userPreferences = userPreferences,
            authViewModel = authViewModel
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
    navigationViewModel: NavigationViewModel,
    userPreferences: UserPreferences,
    authViewModel: AuthViewModel
) {
    val selectedTag = postViewModel.currentTag
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val items = bottomNavItems
    val selectedItemIndex = navigationViewModel.selectedItemIndex

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navController.navigate(CreatePostScreenNav)},
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
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController.navigate(LoginScreenNav) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                authViewModel.resetLoginState()
                                userPreferences.clear()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign Out"
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.width(32.dp)) { }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex.value == index,
                        onClick = {
                            navigationViewModel.updateSelectedItemIndex(index)
                            when (item.title) {
                                "Posts" -> navController.navigate(PostsScreenNav)
                                "Activity Log" -> navController.navigate(ActivityLogScreenNav)
                                "My Posts" -> navController.navigate(MyPostsScreenNav)
                            }

                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex.value) {
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
                    val isSelected = selectedTag == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { postViewModel.toggleTag(null) },
                        label = { Text("All") },
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            enabled = true,
                            selected = isSelected
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFF5F5F5), // Light Grey (Unselected)
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer, // Primary (Selected)
                            labelColor = Color.Black,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(50) // Fully rounded
                    )
                }

                items(PostTag.entries.toTypedArray()) { tag ->
                    val isSelected = selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { postViewModel.toggleTag(tag) },
                        label = { Text(tag.name) },
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            enabled = true,
                            selected = isSelected
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFF5F5F5),
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = Color.Black,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(50)
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
                        onClick = {navController.navigate(PostDetailsScreenNav(post.id))}
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: PostResponse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
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

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = post.tag.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTTOM ROW (Time & Stats) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                // Pushes Time to start (Left) and Stats to end (Right)
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. TIME (Bottom Left)
                Text(
                    text = formatConciseTime(post.createdAt), // <--- UPDATED HERE
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline // Light grey text
                )

                // 2. STATS (Bottom Right)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between Likes and Views
                ) {
                    // Likes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val likeColor = Color(0xFFE91E63)
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Likes",
                            modifier = Modifier.size(18.dp),
                            tint = likeColor
                        )
                        Text(
                            text = post.likeCount.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = likeColor
                        )
                    }

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
}


//@Preview(showBackground = true)
//@Composable
//fun PostItemPreview() {
//    MaterialTheme {
//        PostItem(
//            post = PostResponse(
//                id = "1",
//                title = "Sample Post Title",
//                content = "This is a sample post content that demonstrates how the post item looks in the UI. It can be longer to show how text wraps.",
//                tag = "TECH",
//                createdAt = System.currentTimeMillis(),
//                authorId = "user123",
//                likeCount = 42,
//                viewCount = 156
//            ),
//            modifier = Modifier.padding(16.dp),
//            onClick = { }
//        )
//    }
//}