package dev.warvdine.qotddiscordbot.controllers

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.AuditFields
import dev.warvdine.qotddiscordbot.persistence.UserStats
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.time.Instant

class CreateUsersStatsController(kMongoDatabaseClient: CoroutineDatabase) : Logging {

    private val logger = getLogger()
    private val usersStatsClient = kMongoDatabaseClient.getCollection<UserStats>("UserStats")

    suspend fun createUsersStats(usersStatsToCreate: List<UserStats>): List<String> {
        logger.info("Create Users: {}", usersStatsToCreate)

        val usersStatsWithAuditFields = usersStatsToCreate.map {
            val userStatsWithAuditFields = it.copy()
            userStatsWithAuditFields.auditFields = AuditFields(createdAt = Instant.now(), lastModifiedAt = Instant.now())
            userStatsWithAuditFields
        }

        val result = usersStatsClient.insertMany(usersStatsWithAuditFields)
        return result.insertedIds.values.map { it.toString() }
    }
}
