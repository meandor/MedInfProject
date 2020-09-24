package com.github.meandor.doctorfate.user.presentation
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.user.domain.UserService
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class UserController(salt: String, userService: UserService) extends Controller with LazyLogging {
  override def routes: Route = path("user") {
    post {
      entity(as[UserDTO]) { handleCreateUserRequest }
    }
  }

  def handleCreateUserRequest(userDTO: UserDTO): Route = {
    logger.info("Got user creation request")
    if (userDTO.email.trim.isEmpty) {
      invalidRequestResponse
    } else {
      val hashedPassword = hashPassword(userDTO.password)
      val createdToken   = userService.createUser(userDTO.email, hashedPassword, userDTO.name)
      onSuccess(createdToken) { _ => complete(StatusCodes.Created, userDTO) }
    }
  }

  def hashPassword(password: String): String = {
    val messageDigest = MessageDigest.getInstance("SHA-512")
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8))
    val hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8))
    String.format("%032X", new BigInteger(1, hashedPassword))
  }
}
