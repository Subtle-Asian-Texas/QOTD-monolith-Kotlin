package dev.warvdine.qotddiscordbot.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.MessageChannel

val BOT_LOGS_CHANNEL_ID: Snowflake = Snowflake.of("957039166724648982")

class DiscordLoggerAppender : ConsoleAppender<ILoggingEvent>() {

    companion object {
        var discordClient: GatewayDiscordClient? = null
    }

    private fun formatLogStringFromEvent(logEvent: ILoggingEvent): String {
        val rawLogString = String(encoder.encode(logEvent))
        val firstTwoLinesOfString = rawLogString
            .split("\n")
            .take(2)
            .joinToString(separator = "")

        // We subtract 6 in order to make space for backticks
        val truncatedStringToReturn: String =
            if (firstTwoLinesOfString.length > 1000-6) {
                firstTwoLinesOfString.substring(0, 997-6) + "..."
            } else firstTwoLinesOfString

        return "```$truncatedStringToReturn```"
    }

    private fun writeToDiscordLogChannel(logString: String) {
        try {
            discordClient
                ?.getChannelById(BOT_LOGS_CHANNEL_ID)
                ?.ofType(MessageChannel::class.java)
                ?.flatMap { channel: MessageChannel ->
                    channel.createMessage(logString)
                }
                ?.subscribe()
        } catch (exception: Throwable) {
            println("Error Sending to Discord: \n$logString")
        }
    }

    /**
     * Encodes the log to a string and writes to Discord Log Channel.
     *
     * Custom implementation of the [ch.qos.logback.core.OutputStreamAppender.subAppend] method.
     */
    override fun subAppend(event: ILoggingEvent?) {

        // Keeps the same if check here as the OutputStreamAppender class
        if (!isStarted) return

        if (event != null) writeToDiscordLogChannel(formatLogStringFromEvent(event))

        // Call super
        super.subAppend(event)
    }
}