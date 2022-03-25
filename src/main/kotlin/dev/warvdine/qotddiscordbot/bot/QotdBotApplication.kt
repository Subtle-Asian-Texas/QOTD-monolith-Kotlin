package dev.warvdine.qotddiscordbot.bot

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
    private val applicationContext: ApplicationContext = AnnotationConfigApplicationContext(dev.warvdine.qotddiscordbot.AppConfig::class.java),
    private val discordBot: DiscordClient = applicationContext.getBean(DiscordClient::class.java),
    private val qotdBot: QotdBot = applicationContext.getBean(QotdBot::class.java),
) : Logging {

    private val logger: Logger = getLogger()

    private fun errorHandler(error: Throwable): Publisher<out Void> {
        logger.error("Error found: {}", error)
        return Mono.empty()
    }

    fun start() {
        logger.info("Starting Bot . . .")

        val client = discordBot.login().block()!!

        client.on(ReadyEvent::class.java)
            .flatMap {
                qotdBot.onReadyEvent(it)
                Mono.empty<Void>()
            }
            .onErrorResume(::errorHandler)
            .subscribe()

        client
            .on(MessageCreateEvent::class.java)
            .flatMap {
                qotdBot.onMessageCreateEvent(it)
                Mono.empty<Void>()
            }
            .onErrorResume(::errorHandler)
            .subscribe()

        client.onDisconnect().block()
    }
}

fun main() {
    QotdBotApplication().start()
}
