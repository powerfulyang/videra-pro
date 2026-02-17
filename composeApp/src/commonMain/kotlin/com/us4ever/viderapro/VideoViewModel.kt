package com.us4ever.viderapro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

expect class VideoRepository() {
    suspend fun fetchVideos(): List<VideoItem>
}

class VideoViewModel : ViewModel() {
    private val repository = VideoRepository()

    var videos by mutableStateOf<List<VideoItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isGridView by mutableStateOf(getSetting("is_grid_view", "false").toBoolean())
        private set

    fun loadVideos() {
        viewModelScope.launch {
            isLoading = true
            videos = repository.fetchVideos()
            isLoading = false
        }
    }

    fun toggleViewMode() {
        isGridView = !isGridView
        saveSetting("is_grid_view", isGridView.toString())
    }
}
