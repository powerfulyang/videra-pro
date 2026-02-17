package com.us4ever.viderapro

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OpenListRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    var baseUrl: String = ""

    suspend fun listFiles(path: String): OpenListResponse<OpenListData>? {
        if (baseUrl.isEmpty()) return null
        return try {
            client.post("${normalizeBaseUrl(baseUrl)}/api/fs/list") {
                contentType(ContentType.Application.Json)
                setBody(OpenListListRequest(path = path))
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFile(path: String): OpenListResponse<OpenListData>? {
        if (baseUrl.isEmpty()) return null
        return try {
            client.post("${normalizeBaseUrl(baseUrl)}/api/fs/get") {
                contentType(ContentType.Application.Json)
                setBody(OpenListGetRequest(path = path))
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun normalizeBaseUrl(url: String): String {
        var normalized = url.trim()
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://$normalized"
        }
        return normalized.removeSuffix("/")
    }
}
