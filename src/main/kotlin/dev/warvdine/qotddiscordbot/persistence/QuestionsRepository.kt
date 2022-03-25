package dev.warvdine.qotddiscordbot.persistence

import java.time.Instant

//private val QUESTIONS_DATABASE = kMongoCoroutineDatabase.getCollection<Question>("Questions")

data class QuestionStats(
    /** Number of comments marked as an answer for this question */
    var answersReceived: UInt?,
    /** Number of total comments in the thread for this question */
    var totalCommentsReceivedInThread: UInt?
)

data class AuditFields(
    /** Datetime of when the Item with the AuditField was created. */
    var createdAt: Instant?,
    /** Datetime of when the Item with the AuditField was last modified. */
    var lastModifiedAt: Instant?,
)

data class Question(
    /** ID of the discord message containing the question */
    var _id: String?,
    /**ID of the discord channel/thread containing the question */
    var channelId: String?,
    /** ID of the discord server containing the question */
    var serverId: String?,
    /** Text of the message */
    var messageText: String?,
    /** Type of the question */
    var questionType: String?,
    /** User that posted the question */
    var userId: String?,
    /** ID of the thread where the question was asked */
    var threadId: String?,
    /** List of tags for the question ‘theme’ */
    var tags: List<String>?,
    /** Extra metadata related to the question/thread */
    var questionStats: QuestionStats?,
    /** Audit Fields for the Question */
    var auditFields: AuditFields?,
)

//suspend fun createQuestion(
//    questionToCreate: Question,
//    questionsDatabase: CoroutineCollection<Question> = QUESTIONS_DATABASE
//) {
//    questionToCreate.auditFields = AuditFields(Instant.now(), Instant.now())
//    questionsDatabase.insertOne(questionToCreate)
//}