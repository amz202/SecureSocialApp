package com.example.securesocialapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

typealias PostsUiState = BaseUiState<List<PostViewModel>>
typealias PostUiState = BaseUiState<PostViewModel>
typealias LikeUiState = BaseUiState<Unit>

class PostViewModel(
    private val postRepository: PostRepository,
    private val userPreferences: UserPreferences
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


    fun getAllPosts() {
        viewModelScope.launch {
            postsUiState = BaseUiState.Loading
            try {
                val response = postRepository.getAllPosts()
                _posts.value = response
                postsUiState = BaseUiState.Success(response.map { PostViewModel(postRepository, userPreferences) })
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
                postUiState = BaseUiState.Success(PostViewModel(postRepository, userPreferences))
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
                postsUiState = BaseUiState.Success(response.map { PostViewModel(postRepository, userPreferences) })
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
                postUiState = BaseUiState.Success(PostViewModel(postRepository, userPreferences))

            } catch (e: Exception) {
                postUiState = BaseUiState.Error
            }
        }
    }

}