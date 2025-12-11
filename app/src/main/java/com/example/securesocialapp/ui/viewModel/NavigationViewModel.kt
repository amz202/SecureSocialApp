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
}