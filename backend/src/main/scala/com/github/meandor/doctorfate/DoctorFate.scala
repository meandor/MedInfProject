package com.github.meandor.doctorfate

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.github.meandor.doctorfate.auth.AuthModule
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor

object DoctorFate extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("Starting System")
    implicit val system: ActorSystem[Nothing]               = ActorSystem(Behaviors.empty, "doctorFate")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val databaseModule = DatabaseModule()
    databaseModule.start()
    val jwtIDSecret     = System.getenv("JWT_ID_SECRET")
    val jwtAccessSecret = System.getenv("JWT_ACCESS_SECRET")
    val passwordSalt    = System.getenv("PASSWORD_SALT")
    val authModule      = AuthModule(jwtIDSecret, jwtAccessSecret, passwordSalt, databaseModule)
    val tokenController = authModule.start
    WebServer(tokenController).start()
  }
}
