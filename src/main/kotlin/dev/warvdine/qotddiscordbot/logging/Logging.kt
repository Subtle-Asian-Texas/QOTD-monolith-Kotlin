package dev.warvdine.qotddiscordbot.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.reflect.full.companionObject

/**
 * Base Logging interface that simplifies getting loggers on a class.
 *
 * This code is inspired by ["Idiomatic Logging in Kotlin"](https://www.baeldung.com/kotlin/logging)
 *
 *  Example usage:
 *
 *     import dev.warvdine.qotddiscordbot.logging.Logging
 *     import dev.warvdine.qotddiscordbot.logging.getLogger
 *
 *     class MyClass() : Logging {
 *         val logger: Logger = getLogger()
 *     }
 *
 * The generated logger will use the current class as the logger name.
 */
interface Logging

/**
 * This is an extension method (see [Extensions Functions in Kotlin](https://kotlinlang.org/docs/extensions.html) )
 *
 * The generic parameter T is marked as reified so we can get the class at runtime with ``T::class.java``` here
 * (see [Reified Functions in Kotlin](https://www.baeldung.com/kotlin/reified-functions) )
 *
 * This extension function only applies (and can be used from) instances of the Logging interface.
 */
inline fun <reified T : Logging> T.getLogger(): Logger = getLogger(getClassForLogging(T::class.java))

/**
 * This function takes care of obtaining the "current" class were the [getLogger] function is being used, while
 * still accounting for companion objects. The intention is to use the enclosing class and not the Companion class
 * for the class name.
 */
fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}
