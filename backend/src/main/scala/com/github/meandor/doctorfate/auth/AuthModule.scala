package com.github.meandor.doctorfate.auth
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}
import com.github.meandor.doctorfate.auth.domain.TokenService
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.github.meandor.doctorfate.core.DatabaseModule
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

final case class AuthModule(config: Config, databaseModule: DatabaseModule) extends LazyLogging {
  def start(implicit executionContext: ExecutionContext): TokenController = {
    logger.info("Start loading AuthModule")
    val jwtIDSecret              = config.getString("auth.idTokenSecret")
    val jwtAccessSecret          = config.getString("auth.accessTokenSecret")
    val passwordSalt             = config.getString("auth.passwordSalt")
    val tokenRepository          = new TokenRepository(databaseModule.executionContext)
    val authenticationRepository = new AuthenticationRepository(databaseModule.executionContext)
    val tokenService             = new TokenService(authenticationRepository, tokenRepository)
    val controller               = new TokenController(jwtIDSecret, jwtAccessSecret, passwordSalt, tokenService)
    logger.info("Done loading Token Module")
    controller
  }
}
