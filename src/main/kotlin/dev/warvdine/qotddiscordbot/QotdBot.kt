package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.controllers.CreateCommentsController
import dev.warvdine.qotddiscordbot.controllers.CreateQuestionsController
import dev.warvdine.qotddiscordbot.controllers.UpdateQuestionsController
import dev.warvdine.qotddiscordbot.logging.BOT_LOGS_CHANNEL_ID
import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.Comment
import dev.warvdine.qotddiscordbot.persistence.Question
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.Logger

/**
 * Entry point of the QOTD Discord Bot.
 *
 * This bot is run when the service first loads.
 */
class QotdBot(
    private val createQuestionsController: CreateQuestionsController,
    private val createCommentsController: CreateCommentsController,
    private val updateQuestionsController: UpdateQuestionsController,
    val messageHandler: MessageHandler,
) : Logging {

    private val logger: Logger = getLogger()

    fun onReadyEvent(readyEvent: ReadyEvent) {
        logger.info("Service is ready! logged in as: \"{}\"", readyEvent.self.username)
    }

    fun onMessageCreateEvent(messageCreateEvent: MessageCreateEvent?) {
        if (
            messageCreateEvent?.message?.channelId == BOT_LOGS_CHANNEL_ID
            || messageCreateEvent == null
        ) return

        val messageHandlerResponse = messageHandler.getRequestInfoFromMessage(messageCreateEvent.message)

        if (!messageHandlerResponse.isAServiceRequest) {
            logger.info("New Message: {}", messageCreateEvent.message.content)
            return
        }

        runBlocking(Dispatchers.Default) {
            when (messageHandlerResponse.requestName) {
                "createQuestions" -> {
                    createQuestions(messageCreateEvent, messageHandlerResponse.requestBodyJson!!)
                }
                "createComments" -> {
                    createComments(messageCreateEvent, messageHandlerResponse.requestBodyJson!!)
                }
                "updateQuestions" -> {
                    updateQuestions(messageCreateEvent, messageHandlerResponse.requestBodyJson!!)
                }
                else -> { logger.info("No matching commands found.") }
            }
        }
    }

    suspend fun createQuestions(messageCreateEvent: MessageCreateEvent, requestBodyJson: String) {
        val result = createQuestionsController.createQuestions(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)

        messageCreateEvent
            .message
            .channel
            .flatMap { it.createMessage("Questions created: $result") }
            .subscribe()
    }

    suspend fun createComments(messageCreateEvent: MessageCreateEvent, requestBodyJson: String) {
        val result = createCommentsController.createComments(
            Json.decodeFromString(requestBodyJson)
        )
        updateQuestionsController.updateQuestionStatsFromComments(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)

        messageCreateEvent
            .message
            .channel
            .flatMap { it.createMessage("Comments created: $result") }
            .subscribe()
    }

    suspend fun updateQuestions(messageCreateEvent: MessageCreateEvent, requestBodyJson: String) {
        val result = updateQuestionsController.updateQuestionsFromJson(requestBodyJson)
        logger.info("Result: {}", result)

        messageCreateEvent
            .message
            .channel
            .flatMap { it.createMessage("Questions updated?: $result") }
            .subscribe()
    }
}
