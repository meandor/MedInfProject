package com.github.meandor.doctorfate

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.github.meandor.doctorfate.auth.AuthModule
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

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
    val tokenController =
      AuthModule(jwtIDSecret, jwtAccessSecret, passwordSalt, databaseModule).start

    logger.info("Start composing routes")
    val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

    val exceptionHandler = ExceptionHandler {
      case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
    }

    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)
    val route = handleErrors {
      cors() {
        handleErrors {
          BaseRoutes.routes ~ tokenController.routes
        }
      }
    }
    logger.info("Done composing routes")

    val maybePort: Option[String] = Option(System.getenv("PORT"))
    val defaultPort: Int          = 8080
    val port: Int                 = maybePort.fold(defaultPort)(p => p.toInt)
    val interface: String         = "0.0.0.0"
    logger.info(s"Starting Server at: ${interface} on port: ${port}")
    Http()
      .newServerAt(interface, port)
      .bind(route)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))
    logger.info(s"Server started")
  }
}
