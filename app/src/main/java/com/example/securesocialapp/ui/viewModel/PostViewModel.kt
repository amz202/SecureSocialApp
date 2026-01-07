package com.example.securesocialapp.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.securesocial.data.model.response.PostLikesResponse
import com.example.securesocialapp.data.model.response.PostResponse
import com.example.securesocialapp.SecureSocialApplication
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.model.response.ActivityLog
import com.example.securesocialapp.data.model.response.PostListResponse
import com.example.securesocialapp.data.repository.DashRepository
import com.example.securesocialapp.data.repository.PostRepository
import com.example.securesocialapp.ui.screen.PostTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

typealias PostsUiState = BaseUiState<List<PostListResponse>>
typealias PostUiState = BaseUiState<PostResponse>
typealias LikeUiState = BaseUiState<Unit>
typealias ActivityUiState = BaseUiState<List<ActivityLog>>
typealias PostLikesUiState = BaseUiState<List<PostLikesResponse>>

class PostViewModel(
    private val postRepository: PostRepository,
    private val dashRepository: DashRepository
): ViewModel() {

    var postsUiState: PostsUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var postUiState: PostUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var myPostsUiState: PostsUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var createPostUiState: BaseUiState<PostResponse?> by mutableStateOf(BaseUiState.Success(null)) // Idle is new, or use Loading/Success
        private set
    var likeUiState: LikeUiState by mutableStateOf(BaseUiState.Success(Unit))
        private set

    var activityUiState: ActivityUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var postLikesUiState: PostLikesUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var currentTag: PostTag? by mutableStateOf(null)
        private set

    private val _post = MutableStateFlow<PostResponse?>(null)
    val post: StateFlow<PostResponse?> = _post

    init {
        getAllPosts()
        getActivityLog()
        getMyPosts()
    }

    fun getAllPosts() {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getAllPosts()
                postsUiState = BaseUiState.Success(response)
                Log.d("PostViewModel", "Fetched posts: $response")
            } catch (e: Exception) {
                postsUiState = BaseUiState.Error
                Log.e("PostViewModel", "Error fetching posts", e)
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            likeUiState = BaseUiState.Loading
            try {
                postRepository.likePost(postId)
                val response = postRepository.getPost(postId)
                _post.value = response
                likeUiState = BaseUiState.Success(Unit)
            } catch (e: Exception) {
                likeUiState = BaseUiState.Error
            }
        }
    }

    fun getPost(postId: String) {
        viewModelScope.launch {
            postUiState = BaseUiState.Loading
            try {
                val response = postRepository.getPost(postId)
                _post.value = response
                postUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                postUiState = BaseUiState.Error
            }
        }
    }

    fun getPostsByTag(tagName: String) {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getPostsByTag(tagName)
                postsUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                postsUiState = BaseUiState.Error
            }
        }
    }

    fun toggleTag(tag: PostTag?) {
        currentTag = tag // Update the UI state immediately

        if (tag == null) {
            getAllPosts()
        } else {
            getPostsByTag(tag.name)
        }
    }

    fun createPost(title: String, content: String, tag: PostTag) {
        viewModelScope.launch {
            createPostUiState = BaseUiState.Loading
            try {
                val request = PostRequest(
                    title = title,
                    content = content,
                    tag = tag.name
                )
                val response = postRepository.createPost(request)
                createPostUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                createPostUiState = BaseUiState.Error
            }
        }
    }

    fun getMyPosts() {
        viewModelScope.launch {
            myPostsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getMyPosts()
                myPostsUiState = BaseUiState.Success(response)
             } catch (e: Exception) {
                myPostsUiState = BaseUiState.Error
            }
        }
    }

    fun getActivityLog() {
        viewModelScope.launch {
            activityUiState = BaseUiState.Loading
            try {
                val response = dashRepository.getActivityLog()
                activityUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                activityUiState = BaseUiState.Error
                Log.e("PostViewModel", "Error fetching activity log", e)
            }
        }
    }

    fun getPostLikes(postId: String) {
        viewModelScope.launch {
            postLikesUiState = BaseUiState.Loading
            try {
                val response = postRepository.getPostLikes(postId)
                postLikesUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                postLikesUiState = BaseUiState.Error
            }
        }
    }

    fun resetPostLikesState() {
        postLikesUiState = BaseUiState.Success(emptyList())
    }
    fun resetCreatePostState() {
        createPostUiState = BaseUiState.Success(null)
    }

    companion object {
        val postFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SecureSocialApplication
                PostViewModel(app.container.postRepository, app.container.dashRepository)
            }
        }
    }

}