package dev.warvdine.qotddiscordbot.persistence

import kotlinx.serialization.Contextual
import java.time.Instant

@kotlinx.serialization.Serializable
data class AuditFields(
    /** Datetime of when the Item with the AuditField was created. */
    @Contextual var createdAt: Instant? = Instant.now(),
    /** Datetime of when the Item with the AuditField was last modified. */
    @Contextual var lastModifiedAt: Instant? = Instant.now(),
)
