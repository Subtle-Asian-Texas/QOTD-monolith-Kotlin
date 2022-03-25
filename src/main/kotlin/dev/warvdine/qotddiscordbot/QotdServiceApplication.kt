package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import org.slf4j.Logger
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class QotdServiceApplication(
    private val applicationContext: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java),
    private val discordBot: DiscordClient = applicationContext.getBean(DiscordClient::class.java),
    private val qotdService: QotdService = applicationContext.getBean(QotdService::class.java),
) : Logging {

    private val logger: Logger = getLogger()

    fun start() {
        logger.info("Starting Bot . . .")

        val client = discordBot.login().block()!!

        client.on(ReadyEvent::class.java).subscribe { qotdService.onReadyEvent(it) }
        client.on(MessageCreateEvent::class.java).subscribe { qotdService.onMessageCreateEvent(it) }

        client.onDisconnect().block()
    }
}

fun main() {
    QotdServiceApplication().start()
}
