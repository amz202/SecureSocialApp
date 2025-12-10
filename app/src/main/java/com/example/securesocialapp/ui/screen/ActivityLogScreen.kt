package com.example.securesocialapp.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.securesocialapp.data.model.response.ActivityLog
import com.example.securesocialapp.data.model.response.LogType
import com.example.securesocialapp.ui.navigation.ActivityLogScreenNav
import com.example.securesocialapp.ui.navigation.MyPostsScreenNav
import com.example.securesocialapp.ui.navigation.PostsScreenNav
import com.example.securesocialapp.ui.navigation.navbar.bottomNavItems
import com.example.securesocialapp.ui.screen.common.ErrorScreen
import com.example.securesocialapp.ui.screen.common.LoadingScreen
import com.example.securesocialapp.ui.viewModel.ActivityUiState
import com.example.securesocialapp.ui.viewModel.BaseUiState
import com.example.securesocialapp.ui.viewModel.NavigationViewModel
import com.example.securesocialapp.ui.viewModel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityLogScreen(
    navController: NavHostController,
    viewModel: PostViewModel,
    navigationViewModel: NavigationViewModel,
    activityUiState: ActivityUiState
) {

    LaunchedEffect(Unit) {
        viewModel.getActivityLog()
    }

    when (activityUiState) {
        is BaseUiState.Loading -> LoadingScreen()
        is BaseUiState.Error -> ErrorScreen(onRetry = { viewModel.getActivityLog() })
        is BaseUiState.Success -> ActivityLogList(
            logs = activityUiState.data,
            navController = navController,
            navigationViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogList(
    logs: List<ActivityLog>,
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
                        "Activity Log",
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
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("No activity found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(logs) { log ->
                    ActivityLogItem(log = log)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(
    log: ActivityLog,
    modifier: Modifier = Modifier
) {
    // Determine Icon and Color based on LogType
    val (icon, iconColor) = when (log.action) {
        LogType.LOGIN -> Icons.Default.Login to Color(0xFF4CAF50) // Green
        LogType.LIKE -> Icons.Default.Favorite to Color(0xFFE91E63) // Pink/Red
        LogType.POST -> Icons.Default.Edit to Color(0xFFFF9800) // Orange
        else -> Icons.Default.Info to Color.Gray
    }

    // Use ElevatedCard to match the sleek style of PostItem
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f), // Light pastel background matching the icon
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.action.name.replace("_", " "),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Timestamp
            Text(
                text = formatTimestamp(log.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// Helper to format date
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd\nHH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}