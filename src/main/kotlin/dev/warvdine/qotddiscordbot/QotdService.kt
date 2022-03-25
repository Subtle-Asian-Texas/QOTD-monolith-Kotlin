package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.Question
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger

/**
 * Entry point of the QOTD Service.
 *
 * This should not have any discord specific logic, and only handle JSON from function calls.
 */
class QotdService(kMongoDatabaseClient: CoroutineDatabase) : Logging {

    private val logger: Logger = getLogger()
    private val questionsClient = kMongoDatabaseClient.getCollection<Question>("Questions")

    suspend fun createQuestions(questionListJson: String): List<String> {
        val questionsToCreate = Json.decodeFromString<List<Question>>(questionListJson)
        logger.info("Questions to create: {}", questionsToCreate)

        val result = questionsClient.insertMany(questionsToCreate)
        return result.insertedIds.values.map { it.toString() }
    }
}
