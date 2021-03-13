package com.github.meandor.doctorfate.user.presentation

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.core.presentation.{Controller, PasswordEncryption}
import com.github.meandor.doctorfate.user.domain.{User, UserService}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.util.UUID

class UserController(salt: String, userService: UserService, authenticator: Authenticator[UUID])
    extends Controller
    with PasswordEncryption
    with LazyLogging {
  override def routes: Route = pathPrefix("user") {
    authenticateOAuth2("menstra", authenticator) { userId =>
      pathEndOrSingleSlash {
        delete {
          logger.info("Got user delete request")
          val deletionProcess = userService.delete(userId)
          onSuccess(deletionProcess) { _ => complete(StatusCodes.NoContent) }
        }
      } ~ path("identifiable-data") {
        delete {
          logger.info("Got user anonymization request")
          val anonymizationProcess = userService.anonymize(userId)
          onSuccess(anonymizationProcess) { _ => complete(StatusCodes.NoContent) }
        }
      } ~ path("data") {
        delete {
          logger.info("Got user data deletion request")
          val anonymizationProcess = userService.deleteData(userId)
          onSuccess(anonymizationProcess) { _ => complete(StatusCodes.NoContent) }
        }
      }
    } ~
      path("confirm") {
        post {
          entity(as[ConfirmationDTO]) {
            handleConfirmationRequest
          }
        }
      } ~
      post {
        entity(as[UserDTO]) {
          handleRegisterRequest
        }
      }
  }

  def isValidRequest(userDTO: UserDTO): Boolean = {
    !userDTO.email.trim.isEmpty && !userDTO.password.trim.isEmpty
  }

  def handleRegisterRequest(userDTO: UserDTO): Route = {
    logger.info("Got user registration request")
    if (!isValidRequest(userDTO)) {
      logger.info("Request is invalid")
      Controller.invalidRequestResponse
    } else {
      val hashedPassword = hashPassword(userDTO.password, salt)
      val createdToken = userService.registerUser(
        User(userDTO.email, hashedPassword, userDTO.name, hasVerifiedEmail = false)
      )
      onSuccess(createdToken) {
        case Some(user) =>
          val registeredUser = UserDTO(user.email, user.password, user.name, user.hasVerifiedEmail)
          complete(StatusCodes.Created, registeredUser)
        case None => Controller.failedResponse
      }
    }
  }

  def isValidConfirmationRequest(confirmationDTO: ConfirmationDTO): Boolean = {
    !confirmationDTO.id.trim.isEmpty
  }

  def handleConfirmationRequest(confirmationDTO: ConfirmationDTO): Route = {
    logger.info("Got user confirmation request")

    if (!isValidConfirmationRequest(confirmationDTO)) {
      logger.info("Request is invalid")
      Controller.invalidRequestResponse
    } else {
      val confirmedUser = userService.confirm(confirmationDTO.id)
      onSuccess(confirmedUser) {
        case Some(user) =>
          val confirmedUser = UserDTO(user.email, user.password, user.name, user.hasVerifiedEmail)
          complete(StatusCodes.OK, confirmedUser)
        case None => Controller.invalidRequestResponse
      }
    }
  }
}
