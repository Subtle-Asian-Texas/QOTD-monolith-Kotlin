package dev.warvdine.qotddiscordbot.controllers

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.AuditFields
import dev.warvdine.qotddiscordbot.persistence.Question
import dev.warvdine.qotddiscordbot.persistence.QuestionStats
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger

/**
 * Handles Creating Questions in different methods.
 *
 * This should not have any discord specific logic, and only handle JSON from function calls.
 */
class CreateQuestionsController(kMongoDatabaseClient: CoroutineDatabase) : Logging {

    private val logger: Logger = getLogger()
    private val questionsClient = kMongoDatabaseClient.getCollection<Question>("Questions")

    private fun buildQuestionWithStatsAndAuditFields(question: Question): Question {
        val questionWithStatsAndAuditFields = question.copy()

        if (questionWithStatsAndAuditFields.questionStats == null) {
            questionWithStatsAndAuditFields.questionStats = QuestionStats()
        }
        questionWithStatsAndAuditFields.auditFields = AuditFields()

        return questionWithStatsAndAuditFields
    }

    suspend fun createQuestions(questionList: List<Question>): List<String> {
        logger.info("createQuestions: {}", questionList)

        val questionsToCreate: List<Question> = questionList.map {
            buildQuestionWithStatsAndAuditFields(it)
        }

        logger.info("Questions to create: {}", questionsToCreate)

        val result = questionsClient.insertMany(questionsToCreate)
        return result.insertedIds.values.map { it.toString() }
    }
}
