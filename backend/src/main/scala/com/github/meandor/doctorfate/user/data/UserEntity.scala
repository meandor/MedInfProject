package com.github.meandor.doctorfate.user.data
import java.util.UUID

final case class UserEntity(
    id: UUID,
    email: String,
    password: String,
    name: Option[String],
    emailIsVerified: Boolean
)
