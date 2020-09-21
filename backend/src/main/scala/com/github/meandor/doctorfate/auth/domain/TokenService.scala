package com.github.meandor.doctorfate.auth.domain
import scala.concurrent.Future

class TokenService {
  def createToken(email: String, password: String): Future[Tokens] = ???
}
