package com.github.meandor.doctorfate.auth
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}
import com.github.meandor.doctorfate.auth.domain.TokenService
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.core.{DatabaseModule, Module}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

final case class AuthModule(config: Config, databaseModule: DatabaseModule)(
    implicit executionContext: ExecutionContext
) extends Module {
  def start(): Option[Controller] = {
    logger.info("Start loading AuthModule")
    val jwtIDSecret              = config.getString("auth.idTokenSecret")
    val jwtAccessSecret          = config.getString("auth.accessTokenSecret")
    val passwordSalt             = config.getString("auth.passwordSalt")
    val host                     = config.getString("app.host")
    val tokenRepository          = new TokenRepository(databaseModule.executionContext)
    val authenticationRepository = new AuthenticationRepository(databaseModule.executionContext)
    val tokenService             = new TokenService(authenticationRepository, tokenRepository)
    val controller =
      new TokenController(jwtIDSecret, jwtAccessSecret, passwordSalt, host, tokenService)
    logger.info("Done loading AuthModule")
    Option(controller)
  }
}
