package com.github.meandor.doctorfate.user.domain
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util
import java.util.{Base64, UUID}

import com.github.meandor.doctorfate.user.data.{MailClient, UserEntity, UserRepository}
import com.typesafe.scalalogging.LazyLogging
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import scala.concurrent.{ExecutionContext, Future}

class UserService(
    userRepository: UserRepository,
    mailClient: MailClient,
    confirmationSecret: String,
    confirmationLinkTemplate: String
)(
    implicit ec: ExecutionContext
) extends LazyLogging {

  def toUser(userEntity: UserEntity): User = {
    User(userEntity.email, userEntity.password, userEntity.name, userEntity.emailIsVerified)
  }

  def confirm(id: String): Future[Option[User]] = {
    val toUserWithLogging = (userEntity: UserEntity) => {
      logger.info("Successfully confirmed user")
      toUser(userEntity)
    }
    val email = decrypt(id)

    userRepository
      .confirm(email)
      .map(_.map(toUserWithLogging))
  }

  def secretKey(secret: String): SecretKeySpec = {
    val messageDigest = MessageDigest.getInstance("md5")
    val hashedSecret  = messageDigest.digest(secret.getBytes(StandardCharsets.UTF_8))
    new SecretKeySpec(util.Arrays.copyOf(hashedSecret, 24), "Blowfish")
  }

  def decrypt(cipher: String): String = {
    val cipherBytes        = cipher.getBytes(StandardCharsets.UTF_8)
    val decodedBase64Bytes = Base64.getDecoder.decode(cipherBytes)
    val cypherSystem       = Cipher.getInstance("Blowfish")
    cypherSystem.init(Cipher.DECRYPT_MODE, secretKey(confirmationSecret))
    val plainText = cypherSystem.doFinal(decodedBase64Bytes)
    new String(plainText, StandardCharsets.UTF_8)
  }

  def encrypt(plain: String): String = {
    val cypherSystem = Cipher.getInstance("Blowfish")
    cypherSystem.init(Cipher.ENCRYPT_MODE, secretKey(confirmationSecret))
    val buffer             = cypherSystem.doFinal(plain.getBytes(StandardCharsets.UTF_8))
    val encodedBase64Bytes = Base64.getEncoder.encode(buffer)
    new String(encodedBase64Bytes, StandardCharsets.UTF_8)
  }

  def confirmationLink(recipientMail: String): String = {
    val encryptedMail = encrypt(recipientMail)
    val htmlEncodedId = URLEncoder.encode(encryptedMail, StandardCharsets.UTF_8.toString)
    s"$confirmationLinkTemplate?id=$htmlEncodedId"
  }

  def registerUser(user: User): Future[Option[User]] = {
    userRepository.findByMail(user.email).flatMap {
      case None =>
        for {
          createdUser <- createUser(user)
          emailSent   <- mailClient.sendConfirmationMail(user.email, confirmationLink(user.email))
          _           <- Future.successful(logger.info(s"$emailSent"))
        } yield createdUser
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
