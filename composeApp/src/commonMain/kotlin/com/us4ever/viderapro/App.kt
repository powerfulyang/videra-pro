package com.us4ever.viderapro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview

enum class Screen {
    Local, Remote, Player
}

@Composable
fun App() {
    // The main entry point for the application. 
    // ViewModels are instantiated here using the lifecycle-viewmodel-compose factory.
    val localViewModel: VideoViewModel = viewModel { VideoViewModel() }
    val remoteViewModel: RemoteVideoViewModel = viewModel { RemoteVideoViewModel() }

    AppContent(
        localViewModel = localViewModel,
        remoteViewModel = remoteViewModel
    )
}

@Composable
fun AppContent(
    localViewModel: VideoViewModel,
    remoteViewModel: RemoteVideoViewModel
) {
    // UI logic and state management are handled here. 
    // This allows the Composable to be easily previewed with mock ViewModels.
    var currentScreen by remember { mutableStateOf(Screen.Local) }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    BackHandler(enabled = selectedVideo != null) {
        selectedVideo = null
    }

    BackHandler(enabled = selectedVideo == null && currentScreen == Screen.Remote && remoteViewModel.pathHistory.isNotEmpty()) {
        remoteViewModel.navigateBack()
    }

    PlatformImageLoaderConfig {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (selectedVideo != null) {
                    VideoPlayer(
                        modifier = Modifier,
                        video = selectedVideo!!,
                        onBack = { selectedVideo = null }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.Local,
                                    onClick = { currentScreen = Screen.Local },
                                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                                    label = { Text("Local") }
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.Remote,
                                    onClick = { currentScreen = Screen.Remote },
                                    icon = { Icon(Icons.Default.Cloud, contentDescription = null) },
                                    label = { Text("Remote") }
                                )
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize()) {
                            when (currentScreen) {
                                Screen.Local -> {
                                    PermissionWrapper(
                                        onPermissionGranted = { localViewModel.loadVideos() }
                                    ) {
                                        LocalFilesScreen(
                                            viewModel = localViewModel,
                                            onVideoClick = {
                                                selectedVideo = it
                                            }
                                        )
                                    }
                                }
                                Screen.Remote -> RemoteFilesScreen(
                                    viewModel = remoteViewModel,
                                    onVideoClick = {
                                        selectedVideo = it
                                    }
                                )
                                Screen.Player -> { }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    // A mock version of VideoViewModel to safely use in Previews.
    val mockLocalViewModel = remember {
        object : VideoViewModel() {
            override var videos = emptyList<VideoItem>()
            override var isLoading = false
            override var isGridView = false
            override fun loadVideos() {}
            override fun toggleViewMode() {}
        }
    }

    // A mock version of RemoteVideoViewModel to safely use in Previews.
    val mockRemoteViewModel = remember {
        object : RemoteVideoViewModel() {
            override var remoteFiles = emptyList<OpenListItem>()
            override var isLoading = false
            override var currentPath = "/"
            override var pathHistory = emptyList<String>()
            override var baseUrl = ""
            override fun updateBaseUrl(url: String) {}
            override fun loadPath(path: String) {}
            override fun navigateBack() = false
            override fun navigateToPath(path: String) {}
            override suspend fun getFileUrl(path: String): String? = null
        }
    }

    AppContent(
        localViewModel = mockLocalViewModel,
        remoteViewModel = mockRemoteViewModel
    )
}
