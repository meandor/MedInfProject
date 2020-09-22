package com.github.meandor.doctorfate.auth.data
import java.util.UUID

import scala.concurrent.Future

class AuthenticationRepository {
  def findUserId(email: String, password: String): Future[Option[UUID]] = ???
}
