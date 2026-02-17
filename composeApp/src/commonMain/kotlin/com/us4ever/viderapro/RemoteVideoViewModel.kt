package com.us4ever.viderapro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

expect fun getSetting(key: String, default: String): String
expect fun saveSetting(key: String, value: String)

open class RemoteVideoViewModel : ViewModel() {
    private val repository = OpenListRepository()

    open var remoteFiles by mutableStateOf<List<OpenListItem>>(emptyList())
        protected set

    open var isLoading by mutableStateOf(false)
        protected set

    open var currentPath by mutableStateOf("/")
        protected set

    open var pathHistory by mutableStateOf<List<String>>(emptyList())
        protected set

    open var baseUrl by mutableStateOf(try { getSetting("base_url", "") } catch (e: Exception) { "" })
        protected set

    init {
        try {
            if (baseUrl.isNotEmpty()) {
                repository.baseUrl = baseUrl
                loadPath("/")
            }
        } catch (e: Exception) {
            // Silently fail for preview/initialization issues
        }
    }

    open fun updateBaseUrl(url: String) {
        baseUrl = url
        repository.baseUrl = url
        try {
            saveSetting("base_url", url)
        } catch (e: Exception) {}

        if (url.isNotEmpty()) {
            loadPath("/")
        } else {
            remoteFiles = emptyList()
            currentPath = "/"
            pathHistory = emptyList()
        }
    }

    open fun loadPath(path: String) {
        if (baseUrl.isEmpty()) return
        if (path == currentPath && remoteFiles.isNotEmpty()) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.listFiles(path)
                if (response?.code == 200) {
                    if (path != currentPath) {
                        pathHistory = pathHistory + currentPath
                        currentPath = path
                    }
                    remoteFiles = response.data.content ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    open fun navigateBack(): Boolean {
        if (pathHistory.isNotEmpty()) {
            val prevPath = pathHistory.last()
            pathHistory = pathHistory.dropLast(1)
            currentPath = prevPath
            loadPathWithoutHistory(prevPath)
            return true
        }
        return false
    }

    open fun navigateToPath(path: String) {
        if (path == currentPath) return

        val index = pathHistory.indexOf(path)
        if (index != -1) {
            pathHistory = pathHistory.subList(0, index)
        } else {
            pathHistory = emptyList()
        }
        currentPath = path
        loadPathWithoutHistory(path)
    }

    protected open fun loadPathWithoutHistory(path: String) {
        if (baseUrl.isEmpty()) return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.listFiles(path)
                if (response?.code == 200) {
                    remoteFiles = response.data.content ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    open suspend fun getFileUrl(path: String): String? {
        if (baseUrl.isEmpty()) return null
        return try {
            val response = repository.getFile(path)
            if (response?.code == 200) {
                response.data.raw_url
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
