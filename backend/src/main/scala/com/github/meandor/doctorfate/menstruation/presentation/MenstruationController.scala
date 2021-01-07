package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.core.presentation.{Controller, ErrorDTO}
import com.github.meandor.doctorfate.menstruation.domain.{Menstruation, MenstruationService}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.util.UUID

class MenstruationController(
    authenticator: Authenticator[UUID],
    menstruationService: MenstruationService
) extends Controller
    with LazyLogging {

  def validCreationRequest(menstruationDTO: MenstruationDTO): Boolean = {
    menstruationDTO.start.isEqual(menstruationDTO.end) ||
    menstruationDTO.start.isBefore(menstruationDTO.end)
  }

  def processCreatedMenstruation(maybeCreatedMenstruation: Option[Menstruation]): Route =
    maybeCreatedMenstruation match {
      case Some(menstruation) =>
        complete(
          StatusCodes.OK,
          MenstruationDTO(menstruation.start, menstruation.end)
        )
      case None =>
        complete(
          StatusCodes.BadRequest,
          ErrorDTO("Invalid time frame given. End has to be equal to or after start.")
        )
    }

  def handleMenstruationCreationRequest(userId: UUID)(menstruationDTO: MenstruationDTO): Route = {
    logger.info("Got menstruation creation request")
    if (!validCreationRequest(menstruationDTO)) {
      logger.info("Menstruation creation request is invalid")
      Controller.invalidRequestResponse
    } else {
      val createdMenstruation = menstruationService.create(
        userId,
        Menstruation(menstruationDTO.start, menstruationDTO.end)
      )
      onSuccess(createdMenstruation)(processCreatedMenstruation)
    }
  }

  override def routes: Route = authenticateOAuth2("menstra", authenticator) { userId =>
    post {
      entity(as[MenstruationDTO]) { handleMenstruationCreationRequest(userId) }
    } ~ get {
      logger.info(s"Got request for all menstruation")
      val availableMenstruation = menstruationService.find(userId)
      onSuccess(availableMenstruation) { menstruationForUser =>
        val menstruationDTOs = menstruationForUser.map(menstruation =>
          MenstruationDTO(menstruation.start, menstruation.end)
        )
        complete(StatusCodes.OK, menstruationDTOs)
      }
    }
  }
}
