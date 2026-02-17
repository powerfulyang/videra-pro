package com.us4ever.viderapro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

expect fun getSetting(key: String, default: String): String
expect fun saveSetting(key: String, value: String)

class RemoteVideoViewModel : ViewModel() {
    private val repository = OpenListRepository()

    var remoteFiles by mutableStateOf<List<OpenListItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentPath by mutableStateOf("/")
        private set

    var pathHistory by mutableStateOf<List<String>>(emptyList())
        private set

    var baseUrl by mutableStateOf(getSetting("base_url", ""))
        private set

    init {
        if (baseUrl.isNotEmpty()) {
            repository.baseUrl = baseUrl
            loadPath("/")
        }
    }

    fun updateBaseUrl(url: String) {
        baseUrl = url
        repository.baseUrl = url
        saveSetting("base_url", url)

        if (url.isNotEmpty()) {
            loadPath("/")
        } else {
            remoteFiles = emptyList()
            currentPath = "/"
            pathHistory = emptyList()
        }
    }

    fun loadPath(path: String) {
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

    fun navigateBack(): Boolean {
        if (pathHistory.isNotEmpty()) {
            val prevPath = pathHistory.last()
            pathHistory = pathHistory.dropLast(1)
            currentPath = prevPath
            loadPathWithoutHistory(prevPath)
            return true
        }
        return false
    }

    fun navigateToPath(path: String) {
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

    private fun loadPathWithoutHistory(path: String) {
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

    suspend fun getFileUrl(path: String): String? {
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
