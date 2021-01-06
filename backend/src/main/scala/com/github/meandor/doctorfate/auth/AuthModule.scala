package com.github.meandor.doctorfate.auth
import akka.http.scaladsl.server.directives.Credentials
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.{JWT, JWTVerifier}
import com.github.meandor.doctorfate.auth.data.{AuthenticationRepository, TokenRepository}
import com.github.meandor.doctorfate.auth.domain.TokenService
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.core.{DatabaseModule, Module}
import com.typesafe.config.Config

import java.util.UUID
import scala.concurrent.ExecutionContext

final case class AuthModule(config: Config, databaseModule: DatabaseModule)(
    implicit executionContext: ExecutionContext
) extends Module {
  private val verifier: JWTVerifier = JWT
    .require(Algorithm.HMAC512(config.getString("auth.accessTokenSecret")))
    .withIssuer("doctor-fate")
    .acceptLeeway(1)
    .build()

  private def extractSubject(credentials: Credentials.Provided): Option[UUID] = {
    try {
      val accessToken = verifier.verify(credentials.identifier)
      Some(UUID.fromString(accessToken.getSubject))
    } catch {
      case error: JWTVerificationException =>
        logger.error("Got invalid accessToken", error)
        None
      case error: IllegalArgumentException =>
        logger.error("Got invalid accessToken", error)
        None
    }
  }

  def accessTokenAuthenticator(credentials: Credentials): Option[UUID] =
    credentials match {
      case p @ Credentials.Provided(_) => extractSubject(p)
      case _                           => None
    }

  def start(): Option[Controller] = {
    logger.info("Start loading AuthModule")
    val jwtIDSecret              = config.getString("auth.idTokenSecret")
    val jwtAccessSecret          = config.getString("auth.accessTokenSecret")
    val passwordSalt             = config.getString("auth.passwordSalt")
    val tokenRepository          = new TokenRepository(databaseModule.executionContext)
    val authenticationRepository = new AuthenticationRepository(databaseModule.executionContext)
    val tokenService             = new TokenService(authenticationRepository, tokenRepository)
    val controller =
      new TokenController(jwtIDSecret, jwtAccessSecret, passwordSalt, tokenService)
    logger.info("Done loading AuthModule")
    Option(controller)
  }
}
