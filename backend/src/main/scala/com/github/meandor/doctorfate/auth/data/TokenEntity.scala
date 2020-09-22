package com.github.meandor.doctorfate.auth.data
import java.time.LocalDateTime
import java.util.UUID

final case class TokenEntity(
    userID: UUID,
    email: String,
    name: String,
    emailIsVerified: Boolean,
    createdAt: LocalDateTime,
    expiresAt: LocalDateTime
)
