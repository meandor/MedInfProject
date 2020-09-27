package com.github.meandor.doctorfate.user.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.core.presentation.{Controller, PasswordEncryption}
import com.github.meandor.doctorfate.user.domain.{User, UserService}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class UserController(salt: String, userService: UserService)
    extends Controller
    with PasswordEncryption
    with LazyLogging {
  override def routes: Route = path("user") {
    post {
      entity(as[UserDTO]) { handleRegisterRequest }
    }
  }

  def isValidRequest(userDTO: UserDTO): Boolean = {
    !userDTO.email.trim.isEmpty && !userDTO.password.trim.isEmpty
  }

  def handleRegisterRequest(userDTO: UserDTO): Route = {
    logger.info("Got user registration request")
    if (!isValidRequest(userDTO)) {
      logger.info("Request is invalid")
      invalidRequestResponse
    } else {
      val hashedPassword = hashPassword(userDTO.password, salt)
      val createdToken = userService.registerUser(
        User(userDTO.email, hashedPassword, userDTO.name, hasVerifiedEmail = false)
      )
      onSuccess(createdToken) {
        case Some(user) =>
          val registeredUser = UserDTO(user.email, user.password, user.name, user.hasVerifiedEmail)
          complete(StatusCodes.Created, registeredUser)
        case None => failedResponse
      }
    }
  }
}
