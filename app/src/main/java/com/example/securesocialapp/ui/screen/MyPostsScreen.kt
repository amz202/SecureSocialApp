package com.example.securesocialapp.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.securesocialapp.data.model.response.PostListResponse
import com.example.securesocialapp.data.model.response.PostResponse
import com.example.securesocialapp.ui.navigation.ActivityLogScreenNav
import com.example.securesocialapp.ui.navigation.MyPostsScreenNav
import com.example.securesocialapp.ui.navigation.PostDetailsScreenNav
import com.example.securesocialapp.ui.navigation.PostsScreenNav
import com.example.securesocialapp.ui.navigation.navbar.bottomNavItems
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.NavigationViewModel
import com.example.securesocialapp.ui.viewModel.PostViewModel
import com.example.securesocialapp.ui.viewModel.PostsUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPostsScreen(
    navController: NavHostController,
    postViewModel: PostViewModel,
    postsUiState: PostsUiState,
    navigationViewModel: NavigationViewModel
) {

    LaunchedEffect(Unit) {
        postViewModel.getMyPosts()
    }
    when (postsUiState) {
        is BaseUiState.Success -> MyPostsList(
            posts = postsUiState.data,
            navController = navController,
            navigationViewModel = navigationViewModel
        )
        is BaseUiState.Loading -> LoadingScreen()
        is BaseUiState.Error -> ErrorScreen(
            onRetry = { postViewModel.getMyPosts() }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsList(
    modifier: Modifier = Modifier,
    posts: List<PostListResponse>,
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val items = bottomNavItems
    val selectedItemIndex = navigationViewModel.selectedItemIndex

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Posts",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar{
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
                                imageVector = if (index == selectedItemIndex.value) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (posts.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't posted anything yet.", color = Color.Gray)
            }
        } else {
            // List State
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onClick = {navController.navigate(PostDetailsScreenNav(post.id))}
                    )
                }
            }
        }
    }
}