package com.example.securesocialapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.SecureSocialApplication
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

typealias PostsUiState = BaseUiState<List<PostResponse>>
typealias PostUiState = BaseUiState<PostResponse>
typealias LikeUiState = BaseUiState<Unit>

class PostViewModel(
    private val postRepository: PostRepository
): ViewModel() {

    var postsUiState: PostsUiState = BaseUiState.Loading
        private set

    var postUiState: PostUiState = BaseUiState.Loading
        private set

    var likeUiState: LikeUiState = BaseUiState.Success(Unit)
        private set

    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> = _posts

    private val _post = MutableStateFlow<PostResponse?>(null)
    val post: StateFlow<PostResponse?> = _post

    private val _postsByTag = MutableStateFlow<List<PostResponse>>(emptyList())
    val postsByTag: StateFlow<List<PostResponse>> = _postsByTag

    private val _usersPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val usersPosts: StateFlow<List<PostResponse>> = _usersPosts


    fun getAllPosts() {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getAllPosts()
                _posts.value = response
                postsUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                postsUiState = BaseUiState.Error
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

    companion object {
        val postFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SecureSocialApplication
                PostViewModel(app.container.postRepository)
            }
        }
    }

}