package com.github.meandor.doctorfate.auth.domain
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}

import scala.concurrent.Future

class TokenService(authRepository: AuthenticationRepository, tokenRepository: TokenRepository) {
  def createToken(email: String, password: String): Future[Option[Tokens]] = ???
}
