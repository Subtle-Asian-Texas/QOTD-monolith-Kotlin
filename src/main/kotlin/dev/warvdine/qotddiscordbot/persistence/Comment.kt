package dev.warvdine.qotddiscordbot.persistence

@kotlinx.serialization.Serializable
data class Comment(
    /** ID of the discord message containing the comment */
    var _id: String? = null,
    /** ID of the discord channel containing the comment */
    var channelId: String? = null,
    /** ID of the discord server containing the comment */
    var serverId: String? = null,
    /** message text of the comment */
    var messageText: String? = null,
    /** ID of the discord message containing the question this comment is responding to */
    var questionId: String? = null,
    /** ID of the user that left the comment */
    var userId: String? = null,
    /** ID of the thread that this comment is associated with */
    var threadId: String? = null,
    /** whether this comment is an answer to the associated question */
    var isAnswer: Boolean? = null,
    /** Audit Fields for the comment */
    var auditFields: AuditFields? = null,
)