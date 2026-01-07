package com.example.securesocialapp.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationViewModel : ViewModel() {
    var selectedItemIndex = mutableStateOf(0)
        private set

    fun updateSelectedItemIndex(index: Int) {
        selectedItemIndex.value = index
    }

    private val _showPostLikesDiaglog = MutableStateFlow<Boolean>(false)
    val showPostLikesDialog: MutableStateFlow<Boolean> = _showPostLikesDiaglog
    private val _postId = MutableStateFlow<String>("")
    val postId: MutableStateFlow<String> = _postId

    fun showPostLikesDialog(postId: String) {
        _postId.value = postId
        _showPostLikesDiaglog.value = true
    }
    fun hidePostLikesDialog() {
        _showPostLikesDiaglog.value = false
        _postId.value = ""
    }

    private val _showCommentsDialog = MutableStateFlow<Boolean>(false)
    val showCommentsDialog: MutableStateFlow<Boolean> = _showCommentsDialog
    private val _commentPostId = MutableStateFlow<String>("")
    val commentPostId: MutableStateFlow<String> = _commentPostId

    fun showCommentsDialog(postId: String) {
        _commentPostId.value = postId
        _showCommentsDialog.value = true
    }
    fun hideCommentsDialog() {
        _showCommentsDialog.value = false
        _commentPostId.value = ""
    }
}