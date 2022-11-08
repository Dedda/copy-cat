package org.dedda.copycat.communication

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.dedda.copycat.database.Server

class ClipboardApi(private val server: Server) {

    private val httpClient = HttpClient(CIO) {
        install(Resources)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun sendClipboardPush(clipboardPush: ClipboardPush): ClipboardPushResponse {
        return httpClient.post(server.address + "/push"){
            contentType(ContentType.Application.Json)
            setBody(clipboardPush)
        }.body()
    }
}