package com.github.meandor.doctorfate.auth.presentation

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, path, post}
import akka.http.scaladsl.server.{Route, StandardRoute}
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.auth.domain.{IDToken, TokenService, Tokens}
import com.github.meandor.doctorfate.{Controller, ErrorDTO}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class TokenController(secret: String, salt: String, tokenService: TokenService)
    extends Controller
    with LazyLogging {
  val invalidRequestResponse: StandardRoute =
    complete(StatusCodes.BadRequest, ErrorDTO("Invalid Request"))
  val failedResponse: StandardRoute =
    complete(StatusCodes.InternalServerError)

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
      invalidRequestResponse
    } else {
      val hashedPassword = hashPassword(tokenRequest.password)
      val createdToken   = tokenService.createToken(tokenRequest.email, hashedPassword)
      onSuccess(createdToken)(processTokens)
    }
  }

  def processTokens(maybeTokens: Option[Tokens]): StandardRoute = {
    maybeTokens.map(generateJWT).fold(invalidRequestResponse) { tokens =>
      complete(StatusCodes.Created, TokenDTO(tokens.idToken))
    }
  }

  def generateJWT(tokens: Tokens): JWTSignedTokens = {
    val algorithm      = Algorithm.HMAC512(secret)
    val now            = LocalDateTime.now()
    val in10Hours      = now.plusHours(10L)
    val tomorrow       = now.plusHours(24L)
    val zone           = ZoneId.of("Europe/Berlin")
    val berlinTimeZone = zone.getRules.getOffset(in10Hours)
    val jwtBuilder = JWT
      .create()
      .withIssuer("doctor-fate")
      .withSubject(tokens.idToken.userID.toString)
    val idJWT = jwtBuilder
      .withExpiresAt(Date.from(in10Hours.toInstant(berlinTimeZone)))
      .withClaim("name", tokens.idToken.name)
      .withClaim("email", tokens.idToken.email)
      .withClaim("email_verified", tokens.idToken.emailIsVerified)
      .sign(algorithm)
    val accessJWT = jwtBuilder
      .withExpiresAt(Date.from(tomorrow.toInstant(berlinTimeZone)))
      .sign(algorithm)
    JWTSignedTokens(idJWT, accessJWT)
  }

  def hashPassword(password: String): String = {
    val messageDigest = MessageDigest.getInstance("SHA-512")
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8))
    val hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8))
    String.format("%032X", new BigInteger(1, hashedPassword))
  }
}
