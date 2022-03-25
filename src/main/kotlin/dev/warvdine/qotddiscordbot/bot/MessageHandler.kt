package dev.warvdine.qotddiscordbot.bot

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger

class MessageHandler : Logging {

    private val logger = getLogger()

    /**
     * Checks if the message is meant to be a call to QotdService.
     *
     * String should be prefixed with "qotd!"
     */
    fun isMessageAServiceCall(message: String): Boolean {
        return message.substring(0, 5).lowercase() == "qotd!"
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
    fun getRequestNameFromMessage(message: String): String {
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
    fun getRequestBody(message: String): String {
        return message
            .split("\n") // split all lines
            .drop(1) // Drop the first line (the line that gives us the request name)
            .joinToString(separator = "") { it.trim() } // Join all lines to one string
    }
}