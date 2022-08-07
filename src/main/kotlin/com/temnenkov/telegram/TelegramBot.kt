package com.temnenkov.telegram

import com.google.gson.annotations.SerializedName
import com.temnenkov.levent.LeventProperties.TG_BOT_TOKEN
import com.temnenkov.levent.LeventProperties.TG_HTTP_CLIENT_CONNECT_TIMEOUT
import com.temnenkov.levent.LeventProperties.TG_HTTP_CLIENT_LONGPOLLING_TIMEOUT
import com.temnenkov.levent.LeventProperties.TG_HTTP_CLIENT_TIMEOUT
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

    fun getUpdates(offset: Long): List<IncomingMessage> {
        val requestBuilder = HttpRequest.newBuilder(URI.create("https://api.telegram.org/${token()}/getUpdates"))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(longPollingTimeout() + 1))
            .POST(HttpRequest.BodyPublishers.ofString(GetUpdatestRequest(offset).toJson()))

        val httpResponse = httpClient.send(
            requestBuilder
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )

        return if (httpResponse.statusCode() != 200) {
            logger.error { "bad status code ${httpResponse.statusCode()} text ${httpResponse.body()}" }
            listOf()
        } else {
            logger.info { "got telegram response ${httpResponse.body()}" }
            val response = httpResponse.body().fromJson(GetUpdatesResponse::class.java)
            response.result.asSequence().filter {
                it.message?.from != null && it.message.text != null
            }.map {
                IncomingMessage(it.message!!.messageId, it.message.from!!.id, it.updateId, it.message.text!!)
            }.toList()
        }
    }

    fun sendReplyMessage(to: Long, replyToMessageId: Long, text: String): SendMesageResponse? {
        val requestBuilder = HttpRequest.newBuilder(URI.create("https://api.telegram.org/${token()}/sendMessage"))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(System.getProperty(TG_HTTP_CLIENT_TIMEOUT, "5").toLong()))
            .POST(HttpRequest.BodyPublishers.ofString(SendMessageRequest(to, text, replyToMessageId).toJson()))

        val httpResponse = httpClient.send(
            requestBuilder.build(),
            HttpResponse.BodyHandlers.ofString()
        )

        return if (httpResponse.statusCode() != 200) {
            logger.error { "bad status code ${httpResponse.statusCode()} text ${httpResponse.body()}" }
            null
        } else {
            logger.info { "got telegram response ${httpResponse.body()}" }
            return httpResponse.body().fromJson(SendMesageResponse::class.java)
        }
    }

    private fun token() = System.getProperty(TG_BOT_TOKEN)

    data class SendMessageRequest(
        @SerializedName("chat_id")
        val chatId: Long,
        val text: String,
        @SerializedName("reply_to_message_id")
        val replyToMessageId: Long? = null
    )

    data class GetUpdatestRequest(
        val offset: Long,
        val timeout: Long = longPollingTimeout()
    )

    data class GetUpdatesResponse(
        val ok: Boolean? = null,
        val result: List<Update> = listOf()
    )

    data class SendMesageResponse(
        val ok: Boolean? = null,
        val result: Message? = null
    )

    data class Update(
        @SerializedName("update_id")
        val updateId: Long,
        val message: Message? = null
    )

    data class IncomingMessage(val messageId: Long, val from: Long, val updateId: Long, val text: String)

    data class Message(
        @SerializedName("message_id")
        val messageId: Long,
        val from: User? = null,
        val text: String? = null
    )

    data class User(val id: Long)

    companion object {
        private fun longPollingTimeout() = System.getProperty(TG_HTTP_CLIENT_LONGPOLLING_TIMEOUT, "5").toLong()
        private val logger = KotlinLogging.logger {}
    }
}
