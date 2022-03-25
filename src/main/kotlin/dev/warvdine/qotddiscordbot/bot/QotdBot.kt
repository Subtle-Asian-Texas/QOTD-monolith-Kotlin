package dev.warvdine.qotddiscordbot.bot

import dev.warvdine.qotddiscordbot.QotdService
import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import kotlin.reflect.KFunction

/**
 * Entry point of the QOTD Discord Bot.
 *
 * This bot is run when the service first loads.
 */
class QotdBot(
    private val qotdService: QotdService,
    val messageHandler: MessageHandler,
) : Logging {

    private val logger: Logger = getLogger()

    fun onReadyEvent(readyEvent: ReadyEvent) {
        logger.info("Service is ready! logged in as: \"{}\"", readyEvent.self.username)
    }

    fun onMessageCreateEvent(messageCreateEvent: MessageCreateEvent?) {
        logger.info("Message Created: {}", messageCreateEvent?.message?.content ?: "Not found")

        val messageContent = messageCreateEvent?.message?.content ?: return

        if (!messageHandler.isMessageAServiceCall(messageContent)) return
        val requestName = messageHandler.getRequestNameFromMessage(messageContent)
        val requestBody = messageHandler.getRequestBody(messageContent)

        runBlocking(Dispatchers.Default) {
            val result: Any?
            when (requestName) {
                "createQuestions" -> {
                    logger.info("Calling: {}", requestName)
                    logger.info("Request body: {}", requestBody)
                    result = qotdService.createQuestions(requestBody)
                    logger.info("Result: {}", result)
                }
            }
        }
    }
}