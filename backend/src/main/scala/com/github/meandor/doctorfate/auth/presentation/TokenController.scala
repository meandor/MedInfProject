package com.github.meandor.doctorfate.auth.presentation

import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, path, post, setCookie}
import akka.http.scaladsl.server.Route
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.auth.domain.{TokenService, Tokens}
import com.github.meandor.doctorfate.core.presentation.{Controller, PasswordEncryption}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class TokenController(
    idTokenSecret: String,
    accessTokenSecret: String,
    salt: String,
    host: String,
    tokenService: TokenService
) extends Controller
    with PasswordEncryption
    with LazyLogging {

  override def routes: Route = path("token") {
    post {
      entity(as[TokenRequestDTO]) { handleTokenRequest }
    }
  }

  def isValid(tokenRequest: TokenRequestDTO): Boolean = {
    !tokenRequest.email.trim.isEmpty &&
    !tokenRequest.password.trim.isEmpty &&
    tokenRequest.email.contains("@")
  }

  def handleTokenRequest(tokenRequest: TokenRequestDTO): Route = {
    logger.info("Got token creation request")
    if (!isValid(tokenRequest)) {
      logger.info("Request is invalid")
      Controller.invalidRequestResponse
    } else {
      val hashedPassword = hashPassword(tokenRequest.password, salt)
      val createdToken   = tokenService.createToken(tokenRequest.email, hashedPassword)
      onSuccess(createdToken)(processTokens)
    }
  }

  def processTokens(maybeTokens: Option[Tokens]): Route = {
    maybeTokens.map(generateJWT).fold(Controller.invalidRequestResponse) { tokens =>
      val accessTokenCookie = HttpCookie(
        Controller.accessTokenCookieName,
        value = tokens.accessToken,
        secure = true,
        httpOnly = true,
        domain = Some(host)
      )
      setCookie(accessTokenCookie) {
        complete(StatusCodes.Created, TokenDTO(tokens.idToken))
      }
    }
  }

  def generateJWT(tokens: Tokens): JWTSignedTokens = {
    val idTokenAlgorithm     = Algorithm.HMAC512(idTokenSecret)
    val accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret)
    val now                  = LocalDateTime.now()
    val in10Hours            = now.plusHours(10L)
    val tomorrow             = now.plusHours(24L)
    val zone                 = ZoneId.of("Europe/Berlin")
    val berlinTimeZone       = zone.getRules.getOffset(in10Hours)
    val jwtBuilder = JWT
      .create()
      .withIssuer("doctor-fate")
    val idJWT = jwtBuilder
      .withSubject(tokens.idToken.userID.toString)
      .withExpiresAt(Date.from(in10Hours.toInstant(berlinTimeZone)))
      .withClaim("name", tokens.idToken.name)
      .withClaim("email", tokens.idToken.email)
      .withClaim("email_verified", tokens.idToken.emailIsVerified)
      .sign(idTokenAlgorithm)
    val accessJWT = jwtBuilder
      .withSubject(tokens.accessToken.userID.toString)
      .withExpiresAt(Date.from(tomorrow.toInstant(berlinTimeZone)))
      .sign(accessTokenAlgorithm)
    JWTSignedTokens(idJWT, accessJWT)
  }
}
