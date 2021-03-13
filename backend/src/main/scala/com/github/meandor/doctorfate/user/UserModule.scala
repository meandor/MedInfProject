package com.github.meandor.doctorfate.user

import com.github.meandor.doctorfate.auth.AuthModule
import com.github.meandor.doctorfate.core.{DatabaseModule, Module}
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.menstruation.MenstruationModule
import com.github.meandor.doctorfate.user.data.{MailClient, UserRepository}
import com.github.meandor.doctorfate.user.domain.UserService
import com.github.meandor.doctorfate.user.presentation.UserController
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

final case class UserModule(
    config: Config,
    databaseModule: DatabaseModule,
    authModule: AuthModule,
    menstruationModule: MenstruationModule
)(
    implicit ec: ExecutionContext
) extends Module {
  override def start(): Option[Controller] = {
    logger.info("Start loading UserModule")
    val userRepository           = new UserRepository(databaseModule.executionContext)
    val mailUserName             = config.getString("mail.username")
    val mailPassword             = config.getString("mail.password")
    val mailClient               = new MailClient(mailUserName, mailPassword)
    val confirmationSecret       = config.getString("confirmation.secret")
    val confirmationLinkTemplate = config.getString("confirmation.linkTemplate")
    val userPasswordSalt         = config.getString("auth.passwordSalt")
    val userService = new UserService(
      userRepository,
      mailClient,
      confirmationSecret,
      confirmationLinkTemplate,
      menstruationModule.menstruationService
    )
    val userController = new UserController(
      userPasswordSalt,
      userService,
      authModule.accessTokenAuthenticator
    )
    logger.info("Done loading UserModule")
    Option(userController)
  }
}
