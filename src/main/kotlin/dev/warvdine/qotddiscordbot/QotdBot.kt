package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.controllers.CreateCommentsController
import dev.warvdine.qotddiscordbot.controllers.CreateQuestionsController
import dev.warvdine.qotddiscordbot.controllers.CreateUsersStatsController
import dev.warvdine.qotddiscordbot.controllers.UpdateQuestionsController
import dev.warvdine.qotddiscordbot.logging.BOT_LOGS_CHANNEL_ID
import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
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
    private val createUsersStatsController: CreateUsersStatsController,
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

        var responseMessage: String
        runBlocking(Dispatchers.Default) {
            responseMessage = when (messageHandlerResponse.requestName) {
                "createQuestions" -> {
                    createQuestions(messageHandlerResponse.requestBodyJson!!)
                }
                "createComments" -> {
                    createComments(messageHandlerResponse.requestBodyJson!!)
                }
                "createUsersStats" -> {
                    createUsersStats(messageHandlerResponse.requestBodyJson!!)
                }
                "updateQuestions" -> {
                    updateQuestions(messageHandlerResponse.requestBodyJson!!)
                }
                else -> { "No matching command found for: ${messageHandlerResponse.requestName}" }
            }
        }

        logger.info(responseMessage)

        messageCreateEvent
            .message
            .channel
            .flatMap { it.createMessage(responseMessage) }
            .subscribe()
    }

    private suspend fun createQuestions(requestBodyJson: String): String {
        val result = createQuestionsController.createQuestions(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)

        return "Questions created: $result"
    }

    private suspend fun createComments(requestBodyJson: String): String {
        val result = createCommentsController.createComments(
            Json.decodeFromString(requestBodyJson)
        )
        updateQuestionsController.updateQuestionStatsFromComments(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)

        return "Comments created: $result"
    }

    private suspend fun createUsersStats(requestBodyJson: String): String {
        val result = createUsersStatsController.createUsersStats(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)
        return "UsersStats created: $result"
    }

    private suspend fun updateQuestions(requestBodyJson: String): String {
        val result = updateQuestionsController.updateQuestionsFromQuestionPartials(
            Json.decodeFromString(requestBodyJson)
        )
        logger.info("Result: {}", result)

        return "Questions updated?: $result"
    }
}
