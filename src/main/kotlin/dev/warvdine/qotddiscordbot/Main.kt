package dev.warvdine.qotddiscordbot

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.discordjson.json.gateway.MessageCreate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val LOGGER = LoggerFactory.getLogger(object{}::class.java.`package`.name)

fun setupAndRun(logger: Logger = LOGGER) {
    logger.info("logging in")

    val client: DiscordClient = DiscordClient.create(System.getenv("discord_bot_api_key"))
    val gateway: GatewayDiscordClient = client.login().block()!!

    gateway.on(ReadyEvent::class.java).subscribe { readyEvent ->
        logger.info("Logged in as {}", readyEvent.self.username)
    }

    gateway.on(MessageCreateEvent::class.java).subscribe { messageCreateEvent ->
        logger.info("Message Created: {}", messageCreateEvent.message.content)
    }

    gateway.onDisconnect().block()
}

fun main() {
    setupAndRun()
}