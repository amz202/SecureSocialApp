package com.example.securesocialapp.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    var selectedItemIndex = mutableStateOf(0)
        private set

    fun updateSelectedItemIndex(index: Int) {
        selectedItemIndex.value = index
    }
}