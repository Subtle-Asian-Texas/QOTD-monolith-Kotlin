package dev.warvdine.qotddiscordbot.controllers

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import dev.warvdine.qotddiscordbot.persistence.AuditFields
import dev.warvdine.qotddiscordbot.persistence.Comment
import dev.warvdine.qotddiscordbot.persistence.Question
import dev.warvdine.qotddiscordbot.persistence.QuestionStats
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.set
import java.time.Instant

class UpdateQuestionsController(kMongoDatabaseClient: CoroutineDatabase) : Logging {

    private val logger = getLogger()
    private val questionsClient = kMongoDatabaseClient.getCollection<Question>("Questions")

    suspend fun updateQuestionsFromQuestionPartials(questionPartialsToUpdate: List<Question>): List<String> {
        logger.info("updateQuestions: {}", questionPartialsToUpdate)

        // Validation Logic
        val listOfQuestionErrors: List<String> = questionPartialsToUpdate.mapNotNull {
            if (it._id == null) it.toString()
            else null
        }
        if (listOfQuestionErrors.isNotEmpty()) {
            logger.info("Questions without IDs: ", listOfQuestionErrors)
            return listOfQuestionErrors
        }

        // Updating in DB
        val result: List<String> = questionPartialsToUpdate.map { questionPartial ->
            try {
                val questionPartialWithAudiField = questionPartial.copy()
                questionPartialWithAudiField.auditFields = AuditFields(createdAt = null, lastModifiedAt = Instant.now())
                questionsClient.updateOne(
                    filter = Question::_id eq questionPartialWithAudiField._id!!,
                    target = questionPartialWithAudiField,
                    updateOnlyNotNullProperties = true
                )
                questionPartialWithAudiField._id!!
            } catch (exception: Throwable) {
                logger.error("Error when updating question with id: ${questionPartial._id}", exception)
                "Error with Question ID: ${questionPartial._id}"
            }
        }

        return result
    }

    suspend fun updateQuestionStatsFromComments(commentsToRead: List<Comment>) {
        // Create Hashmap of questionID to number of comments found
        val questionIdToCommentCount: Map<String, UInt> = commentsToRead.fold(mutableMapOf()) { accMap, comment ->
            accMap[comment.questionId!!] = accMap[comment.questionId]?.plus(1U) ?: 1U
            accMap
        }

        questionIdToCommentCount.forEach { (questionId, totalCommentsToIncrement) ->
            val questionFromDB = questionsClient.findOne(Question::_id eq questionId)

            val totalCommentsForQuestion = questionFromDB
                ?.questionStats
                ?.totalCommentsReceivedInThread
                ?.plus(totalCommentsToIncrement)
                ?: totalCommentsToIncrement

            val questionPartialForUpdate = Question(
                questionStats = QuestionStats(
                    totalAnswersReceived = null,
                    totalAnswersReceivedWithin30Hours = null,
                    totalCommentsReceivedInThread = totalCommentsForQuestion
                ),
                auditFields = AuditFields(createdAt = null, lastModifiedAt = Instant.now())
            )

            questionsClient.updateOne(
                filter = Question::_id eq questionId,
                target = questionPartialForUpdate,
                updateOnlyNotNullProperties = true
            )
        }
    }
}