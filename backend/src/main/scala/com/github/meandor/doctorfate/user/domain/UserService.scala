package com.github.meandor.doctorfate.user.domain
import java.util.UUID

import com.github.meandor.doctorfate.user.data.{UserEntity, UserRepository}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserService(userRepository: UserRepository)(implicit ec: ExecutionContext)
    extends LazyLogging {
  def registerUser(user: User): Future[Option[User]] = {
    userRepository.findByMail(user.email).flatMap {
      case None => createUser(user)
      case Some(_) =>
        logger.info("Failed creating user, E-Mail already exists")
        Future.successful(None)
    }
  }

  def createUser(user: User): Future[Option[User]] = {
    val toBeCreatedUser = UserEntity(
      UUID.randomUUID(),
      user.email,
      user.password,
      user.name,
      emailIsVerified = false
    )
    userRepository
      .create(toBeCreatedUser)
      .map { createdUser =>
        logger.info("Successfully created user")
        Option(
          User(
            createdUser.email,
            createdUser.password,
            createdUser.name,
            createdUser.emailIsVerified
          )
        )
      }
  }
}
