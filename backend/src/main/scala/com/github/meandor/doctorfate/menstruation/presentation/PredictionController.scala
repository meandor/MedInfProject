package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.{JWT, JWTVerifier}
import com.github.meandor.doctorfate.core.presentation.Controller
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.time.LocalDate
import java.util.UUID

class PredictionController(secret: String) extends Controller with LazyLogging {
  val algorithm: Algorithm = Algorithm.HMAC512(secret)
  val verifier: JWTVerifier = JWT
    .require(algorithm)
    .withIssuer("doctor-fate")
    .acceptLeeway(1)
    .build()

  def extractSubject(credentials: Credentials.Provided): Option[UUID] = {
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

  override def routes: Route = pathPrefix("menstruation") {
    path("prediction") {
      get {
        authenticateOAuth2("menstra", accessTokenAuthenticator) {
          handlePredictionRequest
        }
      }
    }
  }

  def handlePredictionRequest(userId: UUID): Route = {
    logger.info(s"Got prediction request for user: $userId")
    val ovulation = OvulationDTO(startDate = LocalDate.now(), isActive = false)
    val period    = MenstruationDTO(startDate = LocalDate.now(), isActive = true, duration = 5)
    complete(StatusCodes.OK, PredictionDTO(ovulation, period))
  }
}
