package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import org.slf4j.Logger

class QotdService : Logging {

    private val logger: Logger = getLogger()

    fun onReadyEvent(readyEvent: ReadyEvent) {
        logger.info("Service is ready! logged in as: \"{}\"", readyEvent.self.username)
    }

    fun onMessageCreateEvent(messageCreateEvent: MessageCreateEvent?) {
        logger.info("Message Created: {}", messageCreateEvent?.message?.content ?: "Not found")
    }
}