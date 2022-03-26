package dev.warvdine.qotddiscordbot.controllers

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.AuditFields
import dev.warvdine.qotddiscordbot.persistence.Comment
import dev.warvdine.qotddiscordbot.persistence.Question
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger
import java.time.Instant


/**
 * Handles Creating Questions in different methods.
 *
 * This should not have any discord specific logic, and only handle JSON from function calls.
 */
class CreateCommentsController(kMongoDatabaseClient: CoroutineDatabase) : Logging {

    private val logger: Logger = getLogger()
    private val commentsClient = kMongoDatabaseClient.getCollection<Comment>("Comments")

    suspend fun createComments(commentList: List<Comment>): List<String> {
        logger.info("comments to Create: {}", commentList)

        val commentsToCreate = commentList.map {
            val newComment = it.copy()
            newComment.auditFields = AuditFields(createdAt = Instant.now(), lastModifiedAt = Instant.now())
            newComment
        }

        val result = commentsClient.insertMany(commentsToCreate)
        return result.insertedIds.values.map { it.toString() }
    }
}
