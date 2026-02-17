package com.us4ever.viderapro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFilesScreen(
    viewModel: VideoViewModel,
    onVideoClick: (VideoItem) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "LOCAL VIDEOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            if (viewModel.isGridView) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                )
            )
        },
        // 关键修复：指定内容区域只受状态栏（顶部）和横向影响，避免受底部导航栏影响产生多余空隙
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            } else if (viewModel.videos.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "NO VIDEOS FOUND",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.loadVideos() },
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("REFRESH", style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                    }
                }
            } else {
                val groupedVideos = remember(viewModel.videos) {
                    viewModel.videos.groupBy { formatDate(it.dateAdded) }
                }

                if (viewModel.isGridView) {
                    VideoGrid(groupedVideos, onVideoClick)
                } else {
                    VideoList(groupedVideos, onVideoClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoList(
    groupedVideos: Map<String, List<VideoItem>>,
    onVideoClick: (VideoItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        groupedVideos.forEach { (date, videos) ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                ) {
                    Text(
                        text = date.uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(videos) { video ->
                ListItem(
                    headlineContent = {
                        Text(
                            video.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    },
                    supportingContent = {
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    formatDuration(video.duration),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "|",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                Text(
                                    formatFileSize(video.size),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    ),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                video.path ?: "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp
                                ),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    },
                    leadingContent = {
                        VideoThumbnail(
                            video,
                            Modifier
                                .size(110.dp, 66.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        )
                    },
                    modifier = Modifier.clickable { onVideoClick(video) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoGrid(
    groupedVideos: Map<String, List<VideoItem>>,
    onVideoClick: (VideoItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        groupedVideos.forEach { (date, videos) ->
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = date.uppercase(),
                    modifier = Modifier.padding(bottom = 8.dp, top = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(videos) { video ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVideoClick(video) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16/9f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        VideoThumbnail(
                            video,
                            Modifier.fillMaxSize()
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp),
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(1.dp)
                        ) {
                            Text(
                                text = formatDuration(video.duration),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = video.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    )
                    Text(
                        text = formatFileSize(video.size),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoThumbnail(video: VideoItem, modifier: Modifier) {
    AsyncImage(
        model = video.uri,
        contentDescription = null,
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteFilesScreen(
    viewModel: RemoteVideoViewModel,
    onVideoClick: (VideoItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var urlInput by remember { mutableStateOf(viewModel.baseUrl) }

    LaunchedEffect(viewModel.baseUrl) {
        if (viewModel.baseUrl.isNotEmpty() && viewModel.remoteFiles.isEmpty() && viewModel.currentPath == "/") {
            viewModel.loadPath("/")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Cloud,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "OPENLIST VIDEOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (viewModel.pathHistory.isNotEmpty()) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (viewModel.baseUrl.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateBaseUrl("") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Change Server")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                )
            )
        },
        // 关键修复：同上
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.baseUrl.isEmpty()) {
                // Setup Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Dns,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "CONNECT TO OPENLIST",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Enter your OpenList server address to browse remote media.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(32.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("OpenList URL", style = TextStyle(fontFamily = FontFamily.Monospace)) },
                        placeholder = { Text("http://192.168.1.100:5244", style = TextStyle(fontFamily = FontFamily.Monospace)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.updateBaseUrl(urlInput) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            "CONNECT",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            } else {
                // Files Screen
                Column {
                    // Show current server address
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SOURCE: ${viewModel.baseUrl.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    PathBreadcrumbs(
                        path = viewModel.currentPath,
                        onPathClick = { viewModel.navigateToPath(it) }
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(viewModel.remoteFiles) { item ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                },
                                supportingContent = {
                                    if (!item.is_dir) {
                                        Text(
                                            formatFileSize(item.size),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            ),
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                },
                                leadingContent = {
                                    val iconAndColor = getFileIconAndColor(item)
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = iconAndColor.second.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            iconAndColor.first,
                                            contentDescription = null,
                                            tint = iconAndColor.second,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.clickable(enabled = !viewModel.isLoading) {
                                    val fullPath = if (viewModel.currentPath == "/") "/${item.name}" else "${viewModel.currentPath}/${item.name}"
                                    if (item.is_dir) {
                                        viewModel.loadPath(fullPath)
                                    } else if (isVideoFile(item.name)) {
                                        scope.launch {
                                            val url = viewModel.getFileUrl(fullPath)
                                            if (url != null) {
                                                onVideoClick(
                                                    VideoItem(
                                                        id = item.name.hashCode().toLong(),
                                                        name = item.name,
                                                        duration = 0,
                                                        size = item.size,
                                                        uri = url,
                                                        path = fullPath,
                                                        isRemote = true
                                                    )
                                                )
                                            }
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                        .clickable(enabled = true, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun getFileIconAndColor(item: OpenListItem): Pair<ImageVector, Color> {
    return if (item.is_dir) {
        Icons.Outlined.FolderZip to MaterialTheme.colorScheme.primary
    } else {
        val extension = item.name.substringAfterLast('.', "").lowercase()
        when {
            isVideoFile(item.name) -> Icons.Outlined.SmartDisplay to Color(0xFFE91E63)
            isAudioFile(item.name) -> Icons.Outlined.Headset to Color(0xFF2196F3)
            isImageFile(item.name) -> Icons.Outlined.Collections to Color(0xFF4CAF50)
            extension == "pdf" -> Icons.Outlined.PictureAsPdf to Color(0xFFF44336)
            extension in listOf("zip", "rar", "7z", "tar", "gz") -> Icons.Outlined.Inventory to Color(0xFFFF9800)
            else -> Icons.AutoMirrored.Outlined.InsertDriveFile to MaterialTheme.colorScheme.outline
        }
    }
}

fun isVideoFile(fileName: String): Boolean {
    val extensions = listOf("mp4", "mkv", "avi", "mov", "flv", "wmv", "webm")
    return extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

fun isAudioFile(fileName: String): Boolean {
    val extensions = listOf("mp3", "wav", "flac", "ogg", "m4a", "aac")
    return extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

fun isImageFile(fileName: String): Boolean {
    val extensions = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
    return extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathBreadcrumbs(
    path: String,
    onPathClick: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                val parts = path.split("/").filter { it.isNotEmpty() }

                Text(
                    text = "ROOT",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (path == "/") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable { onPathClick("/") }
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                var currentAccPath = ""
                parts.forEach { part ->
                    Text(
                        ">",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                    currentAccPath += "/$part"
                    val thisPath = currentAccPath
                    Text(
                        text = part.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (thisPath == path) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onPathClick(thisPath) }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        }
    }
}
