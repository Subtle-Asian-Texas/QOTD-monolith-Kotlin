package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.logging.DiscordLoggerAppender
import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import reactor.core.publisher.Mono

class QotdBotApplication(
    private val applicationContext: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java),
    private val discordBot: DiscordClient = applicationContext.getBean(DiscordClient::class.java),
    private val qotdBot: QotdBot = applicationContext.getBean(QotdBot::class.java),
) : Logging {

    private val logger: Logger = getLogger()

    fun start() {
        logger.info("Starting Bot . . .")

        val client = discordBot.login().block()!!

        // Sets the DiscordLoggerAppender so it knows it's been attached
        DiscordLoggerAppender.discordClient = client
        logger.info("Discord Logger Attached.")

        client
            .on(ReadyEvent::class.java)
            .subscribe {
                try {
                    qotdBot.onReadyEvent(it)
                } catch (exception: Throwable) {
                    logger.error("Error found:", exception)
                }
            }

        client
            .on(MessageCreateEvent::class.java)
            .subscribe {
                try {
                    qotdBot.onMessageCreateEvent(it)
                } catch (exception: Throwable) {
                    logger.error("Error found:", exception)
                }
            }

        client.onDisconnect().block()
    }
}

fun main() {
    QotdBotApplication().start()
}
