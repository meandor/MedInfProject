package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.core.presentation.Controller
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.time.LocalDate
import java.util.UUID

class PredictionController(authenticator: Authenticator[UUID]) extends Controller with LazyLogging {
  override def routes: Route = pathPrefix("menstruation") {
    path("prediction") {
      get {
        authenticateOAuth2("menstra", authenticator) {
          handlePredictionRequest
        }
      }
    }
  }

  def handlePredictionRequest(userId: UUID): Route = {
    logger.info(s"Got prediction request for user: $userId")
    val ovulation = OvulationPredictionDTO(startDate = LocalDate.now(), isActive = false)
    val period =
      MenstruationPredictionDTO(startDate = LocalDate.now(), isActive = true, duration = 5)
    complete(StatusCodes.OK, PredictionDTO(ovulation, period))
  }
}
