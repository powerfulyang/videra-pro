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
@Preview
fun App() {
    val localViewModel: VideoViewModel = viewModel { VideoViewModel() }
    val remoteViewModel: RemoteVideoViewModel = viewModel { RemoteVideoViewModel() }

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
