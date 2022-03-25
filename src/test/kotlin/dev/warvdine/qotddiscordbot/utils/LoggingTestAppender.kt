package dev.warvdine.qotddiscordbot.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

/**
 * Appender to be used to assert that the logs are being generated.
 *
 * Use the [getLastLoggedEvent] to get the last generated event.
 *
 * This idea was grabbed from [Don't mock static: test SLF4J Logger with appenders](https://kotlintesting.com/mock-slf4j/)
 */
class LoggingTestAppender : AppenderBase<ILoggingEvent>() {
    private val events: MutableList<ILoggingEvent> = mutableListOf()

    fun getLastLoggedEvent() = events.lastOrNull()

    fun getLastLoggedError() = events.lastOrNull { it.level.isGreaterOrEqual(Level.ERROR) }

    override fun append(eventObject: ILoggingEvent) {
        events.add(eventObject)
    }
}