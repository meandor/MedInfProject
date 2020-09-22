package com.github.meandor.doctorfate.auth.domain
import java.util.UUID

final case class IDToken(userID: UUID, name: String, email: String, emailIsVerified: Boolean)
