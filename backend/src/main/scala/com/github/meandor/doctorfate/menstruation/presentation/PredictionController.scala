package com.github.meandor.doctorfate.menstruation.presentation
import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookiePair
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.{JWT, JWTVerifier}
import com.github.meandor.doctorfate.core.presentation.Controller
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class PredictionController(secret: String) extends Controller with LazyLogging {
  val algorithm: Algorithm = Algorithm.HMAC512(secret)
  val verifier: JWTVerifier = JWT
    .require(algorithm)
    .withIssuer("doctor-fate")
    .acceptLeeway(1)
    .build()

  override def routes: Route = pathPrefix("menstruation") {
    path("prediction") {
      get {
        cookie(Controller.accessTokenCookieName) { handlePredictionRequest }
      }
    }
  }

  def userId(accessTokenValue: String): Option[UUID] = {
    try {
      val accessToken = verifier.verify(accessTokenValue)
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

  def handlePredictionRequest(accessTokenCookie: HttpCookiePair): Route = {
    val maybeUserId = userId(accessTokenCookie.value)
    maybeUserId.fold(Controller.unauthorized) { userId =>
      logger.info("Got prediction request for user: %s", userId)
      val ovulation = OvulationDTO(startDate = LocalDate.now(), isActive = false)
      val period    = PeriodDTO(startDate = LocalDate.now(), isActive = false, duration = 5)
      complete(StatusCodes.OK, PredictionDTO(ovulation, period))
    }
  }
}
