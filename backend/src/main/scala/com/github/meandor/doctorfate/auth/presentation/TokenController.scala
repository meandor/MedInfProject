package com.github.meandor.doctorfate.auth.presentation

import java.time.{LocalDateTime, ZoneId, ZoneOffset}
import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, path, post}
import akka.http.scaladsl.server.Route
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.Controller
import com.github.meandor.doctorfate.auth.domain.{IDToken, TokenService, Tokens}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.Future

class TokenController(secret: String, tokenService: TokenService) extends Controller {
  override def routes: Route = path("token") {
    post {
      entity(as[TokenRequestDTO]) { handleTokenRequest }
    }
  }

  def handleTokenRequest(tokenRequest: TokenRequestDTO): Route = {
    val createdToken: Future[Tokens] =
      tokenService.createToken(tokenRequest.email, tokenRequest.password)
    onSuccess(createdToken) { token =>
      val jwtIdToken = generateJWT(token.idToken)
      complete(StatusCodes.Created, TokenDTO(jwtIdToken))
    }
  }

  def generateJWT(token: IDToken): String = {
    val algorithm  = Algorithm.HMAC512(secret)
    val now        = LocalDateTime.now()
    val expiry     = now.plusHours(10L)
    val zone       = ZoneId.of("Europe/Berlin")
    val zoneOffSet = zone.getRules.getOffset(expiry)
    JWT
      .create()
      .withIssuer("doctor-fate")
      .withExpiresAt(Date.from(expiry.toInstant(zoneOffSet)))
      .withClaim("name", token.name)
      .withClaim("email", token.email)
      .withClaim("email_verified", token.emailIsVerified)
      .sign(algorithm)
  }
}
