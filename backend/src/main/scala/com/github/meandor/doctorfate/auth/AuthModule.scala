package com.github.meandor.doctorfate.auth
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}
import com.github.meandor.doctorfate.auth.domain.TokenService
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

object AuthModule extends LazyLogging {
  def start(
      jwtIDSecret: String,
      jwtAccessSecret: String,
      passwordSalt: String,
      databaseEC: ExecutionContext
  )(implicit executionContext: ExecutionContext): TokenController = {
    logger.info("Start loading AuthModule")
    val tokenRepository          = new TokenRepository(databaseEC)
    val authenticationRepository = new AuthenticationRepository(databaseEC)
    val tokenService             = new TokenService(authenticationRepository, tokenRepository)
    val controller               = new TokenController(jwtIDSecret, jwtAccessSecret, passwordSalt, tokenService)
    logger.info("Done loading Token Module")
    controller
  }
}
