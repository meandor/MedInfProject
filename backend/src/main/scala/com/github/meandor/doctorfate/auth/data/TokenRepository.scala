package com.github.meandor.doctorfate.auth.data
import java.util.UUID

import scala.concurrent.Future

class TokenRepository {
  def create(userID: UUID): Future[TokenEntity] = ???
}
