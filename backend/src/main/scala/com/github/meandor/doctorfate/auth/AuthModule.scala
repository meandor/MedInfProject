package com.github.meandor.doctorfate.auth
import com.github.meandor.doctorfate.DatabaseModule
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}
import com.github.meandor.doctorfate.auth.domain.TokenService
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

final case class AuthModule(
    jwtIDSecret: String,
    jwtAccessSecret: String,
    passwordSalt: String,
    databaseModule: DatabaseModule
) extends LazyLogging {
  def start(implicit executionContext: ExecutionContext): TokenController = {
    logger.info("Start loading AuthModule")
    val tokenRepository          = new TokenRepository(databaseModule.executionContext)
    val authenticationRepository = new AuthenticationRepository(databaseModule.executionContext)
    val tokenService             = new TokenService(authenticationRepository, tokenRepository)
    val controller               = new TokenController(jwtIDSecret, jwtAccessSecret, passwordSalt, tokenService)
    logger.info("Done loading Token Module")
    controller
  }
}
