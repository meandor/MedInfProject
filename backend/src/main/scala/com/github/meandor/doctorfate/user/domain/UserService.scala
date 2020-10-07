package com.github.meandor.doctorfate.user.domain
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

import com.github.meandor.doctorfate.user.data.{MailClient, UserEntity, UserRepository}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserService(
    userRepository: UserRepository,
    mailClient: MailClient,
    confirmationSalt: String,
    confirmationLinkTemplate: String
)(
    implicit ec: ExecutionContext
) extends LazyLogging {

  def confirm(id: String): Future[Option[User]] = ???

  def confirmationLink(recipientMail: String): String = {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(confirmationSalt.getBytes(StandardCharsets.UTF_8))
    val hashedPassword = messageDigest.digest(recipientMail.getBytes(StandardCharsets.UTF_8))
    val hash           = String.format("%032X", new BigInteger(1, hashedPassword))
    s"$confirmationLinkTemplate?id=$hash"
  }

  def registerUser(user: User): Future[Option[User]] = {
    userRepository.findByMail(user.email).flatMap {
      case None =>
        val createdUser = createUser(user)
        mailClient.sendConfirmationMail(user.email, confirmationLink(user.email))
        createdUser
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
