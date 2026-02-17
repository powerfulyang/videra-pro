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

open class VideoViewModel : ViewModel() {
    private val repository = VideoRepository()

    open var videos by mutableStateOf<List<VideoItem>>(emptyList())
        protected set

    open var isLoading by mutableStateOf(false)
        protected set

    open var isGridView by mutableStateOf(try { getSetting("is_grid_view", "false").toBoolean() } catch(e: Exception) { false })
        protected set

    open fun loadVideos() {
        viewModelScope.launch {
            isLoading = true
            videos = repository.fetchVideos()
            isLoading = false
        }
    }

    open fun toggleViewMode() {
        isGridView = !isGridView
        saveSetting("is_grid_view", isGridView.toString())
    }
}
