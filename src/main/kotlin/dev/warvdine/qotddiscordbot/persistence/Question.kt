package dev.warvdine.qotddiscordbot.persistence

@kotlinx.serialization.Serializable
data class QuestionStats(
    /** Number of comments marked as an answer for this question */
    var totalAnswersReceived: UInt? = 0U,
    /** Number of comments marked as an answer for this question within 30 hours from question being asked */
    var totalAnswersReceivedWithin30Hours: UInt? = 0U,
    /** Number of total comments in the thread for this question */
    var totalCommentsReceivedInThread: UInt? = 0U
)

@kotlinx.serialization.Serializable
data class Question(
    /** ID of the discord message containing the question */
    var _id: String? = null,
    /**ID of the discord channel/thread containing the question */
    var channelId: String? = null,
    /** ID of the discord server containing the question */
    var serverId: String? = null,
    /** Text of the message */
    var messageText: String? = null,
    /** Type of the question */
    var questionType: String? = null,
    /** User that posted the question */
    var userId: String? = null,
    /** ID of the thread where the question was asked */
    var threadId: String? = null,
    /** List of tags for the question ‘theme’ */
    var tags: List<String>? = null,
    /** Extra metadata related to the question/thread */
    var questionStats: QuestionStats? = null,
    /** Audit Fields for the Question */
    var auditFields: AuditFields? = null,
)
