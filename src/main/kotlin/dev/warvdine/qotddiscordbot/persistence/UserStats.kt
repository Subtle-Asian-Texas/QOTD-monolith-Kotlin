package dev.warvdine.qotddiscordbot.persistence

@kotlinx.serialization.Serializable
data class UserStats(
    /** ID of the User */
    var _id: String? = null,
    /** Username of the User */
    var discordUsername: String? = null,
    /** Nickname of the User on the discord server */
    var serverNickname: String? = null,
    /** Number of Questions answered with type 'QOTD' */
    var questionsOfTheDayAnswered: String? = null,
    /** Total Number of questions answered overall */
    var totalQuestionsAnswered: String? = null,
    /** Start of current streak */
    var startOfStreak: String? = null,
    /** Count of how many days the user has consecutively answered a question */
    var currentStreakCount: String? = null,
    /** Audit Fields for the Question */
    var auditFields: AuditFields? = null,
)