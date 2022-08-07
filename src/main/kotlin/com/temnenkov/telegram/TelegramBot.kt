package com.temnenkov.telegram

import com.google.gson.annotations.SerializedName
import com.temnenkov.levent.LeventProperties.TG_BOT_TOKEN
import com.temnenkov.levent.LeventProperties.TG_HTTP_CLIENT_CONNECT_TIMEOUT
import com.temnenkov.levent.LeventProperties.TG_HTTP_CLIENT_LONGPOLLING_TIMEOUT
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson
import mu.KotlinLogging
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class TelegramBot {

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(System.getProperty(TG_HTTP_CLIENT_CONNECT_TIMEOUT, "5").toLong()))
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    fun getUpdates(offset: Int): List<IncomingMessage> {
        val requestBuilder = HttpRequest.newBuilder(URI.create("https://api.telegram.org/${token()}/getUpdates"))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(longPollingTimeout().toLong() + 1))
            .POST(HttpRequest.BodyPublishers.ofString(GetUpdatestRequest(offset).toJson()))

        val httpResponse = httpClient.send(
            requestBuilder
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )

        if (httpResponse.statusCode() != 200) {
            logger.error { "bad status code ${httpResponse.statusCode()} text ${httpResponse.body()}" }
            return listOf()
        } else {
            logger.info { "got telegram response ${httpResponse.body()}" }
            val response = httpResponse.body().fromJson(GetUpdatesResponse::class.java)
            return response.result.asSequence().filter {
                it.message?.from != null && it.message.text != null
            }.map {
                IncomingMessage(it.message!!.from!!.id, it.updateId, it.message.text!!)
            }.toList()
        }
    }

    fun sendReplyMessage(to: Int, replyToMessageId: Int, text: String) {
        TODO()
    }

    private fun token() = System.getProperty(TG_BOT_TOKEN)

    data class GetUpdatestRequest(
        val offset: Int,
        val timeout: Int = longPollingTimeout()
    )

    data class GetUpdatesResponse(
        val ok: Boolean? = null,
        val result: List<Update> = listOf()
    )

    data class Update(
        @SerializedName("update_id")
        val updateId: Int,
        val message: Message? = null
    )

    data class IncomingMessage(val from: Int, val updateId: Int, val text: String)

    data class Message(
        @SerializedName("message_id")
        val messageId: Int,
        val from: User? = null,
        val text: String? = null
    )

    data class User(val id: Int)

    companion object {
        private fun longPollingTimeout() = System.getProperty(TG_HTTP_CLIENT_LONGPOLLING_TIMEOUT, "5").toInt()
        private val logger = KotlinLogging.logger {}
    }
}
