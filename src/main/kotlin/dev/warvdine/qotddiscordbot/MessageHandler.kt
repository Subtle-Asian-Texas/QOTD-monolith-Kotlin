package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.logging.BOT_LOGS_CHANNEL_ID
import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent

data class MessageHandlerResponse(
    /** Returns whether the message is a service request or not */
    val isAServiceRequest: Boolean = false,
    /** The name of the request found in the message */
    val requestName: String? = null,
    /** The portion of the message to be used as the request body */
    val requestBodyJson: String? = null,
)

class MessageHandler : Logging {

    private val logger = getLogger()

    /**
     * Checks if the message is meant to be a call to QotdService.
     *
     * String should be prefixed with "qotd!"
     */
    private fun isMessageAServiceCall(message: String): Boolean {
        return message.take(5).lowercase() == "qotd!"
    }

    /**
     * Gets the name of the endpoint to call in QotdService.
     *
     * Examples:
     *
     *     qotd!createQuestions -> QotdService/createQuestions
     *     qotd!updateQuestion  -> QotdService/updateQuestions
     *     qotd!createAnswers   -> QotdService/createAnswers
     */
    private fun getRequestNameFromMessage(message: String): String {
        return message
            .split("\n") // split all lines
            .first() // Grab the first line
            .substring(5) // grab the string without the "qotd!" prefix
    }

    /**
     * Returns the request body of the message.
     *
     * Example:
     *
     *     qotd!createQuestions
     *     ["https.discord.com/channels/123/123/123123",
     *     "https.discord.com/channels/123/123/123456"]
     *
     *     request body = ["https.discord.com/channels/123/123/123123", "https.discord.com/channels/123/123/123456"]
     */
    private fun getRequestBody(message: String): String {
        return message
            .split("\n") // split all lines
            .drop(1) // Drop the first line (the line that gives us the request name)
            .joinToString(separator = "") { it.trim() } // Join all lines to one string
    }

    fun getRequestInfoFromMessage(message: Message): MessageHandlerResponse {
        val messageText = message.content
        return if (isMessageAServiceCall(messageText)) {
            MessageHandlerResponse(
                isAServiceRequest = true,
                requestName = getRequestNameFromMessage(messageText),
                requestBodyJson = getRequestBody(messageText)
            )
        } else {
            MessageHandlerResponse(isAServiceRequest = false)
        }
    }
}
