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
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.SecureSocialApplication
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.model.response.ActivityLog
import com.example.securesocialapp.data.repository.DashRepository
import com.example.securesocialapp.data.repository.PostRepository
import com.example.securesocialapp.ui.screen.PostTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

typealias PostsUiState = BaseUiState<List<PostResponse>>
typealias PostUiState = BaseUiState<PostResponse>
typealias LikeUiState = BaseUiState<Unit>
typealias ActivityUiState = BaseUiState<List<ActivityLog>>

class PostViewModel(
    private val postRepository: PostRepository,
    private val dashRepository: DashRepository
): ViewModel() {

    var postsUiState: PostsUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var postUiState: PostUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var likeUiState: LikeUiState by mutableStateOf(BaseUiState.Success(Unit))
        private set

    var activityUiState: ActivityUiState by mutableStateOf(BaseUiState.Loading)
        private set

    var currentTag: PostTag? by mutableStateOf(null)
        private set

    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> = _posts

    private val _post = MutableStateFlow<PostResponse?>(null)
    val post: StateFlow<PostResponse?> = _post

    private val _postsByTag = MutableStateFlow<List<PostResponse>>(emptyList())
    val postsByTag: StateFlow<List<PostResponse>> = _postsByTag

    private val _usersPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val usersPosts: StateFlow<List<PostResponse>> = _usersPosts

    init {
        getAllPosts()
        getActivityLog()
    }

    fun getAllPosts() {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getAllPosts()
                _posts.value = response
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
                _postsByTag.value = response
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

    fun createPost(post: PostRequest){
        viewModelScope.launch {
            postUiState = BaseUiState.Loading
            try {
                val response = postRepository.createPost(post)
                _post.value = response
                postUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                postUiState = BaseUiState.Error
            }
        }
    }

    fun getMyPosts() {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getMyPosts()
                _usersPosts.value = response
                postsUiState = BaseUiState.Success(response)
             } catch (e: Exception) {
                postsUiState = BaseUiState.Error
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