package com.github.meandor.doctorfate.core
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, MissingCookieRejection, RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{cors, corsRejectionHandler}
import com.github.meandor.doctorfate.core.presentation.{BaseRoutes, Controller}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

final case class WebServer(config: Config, tokenController: Controller, userController: Controller)(
    implicit executionContext: ExecutionContextExecutor,
    system: ActorSystem[Nothing]
) extends Module {
  val rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case MissingCookieRejection(tokenController.ACCESS_TOKEN_COOKIE_NAME) =>
          tokenController.unauthorized
      }
      .result()

  val rejectionHandlerWithCors: RejectionHandler =
    corsRejectionHandler.withFallback(rejectionHandler).withFallback(RejectionHandler.default)

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
    case e: Exception =>
      logger.error("Error processing request", e)
      tokenController.failedResponse
  }

  def routes(): Route = {
    logger.info("Start composing routes")
    val handleErrors =
      handleRejections(rejectionHandlerWithCors) & handleExceptions(exceptionHandler)
    val route = handleErrors {
      cors() {
        handleErrors {
          BaseRoutes.routes ~ tokenController.routes ~ userController.routes
        }
      }
    }
    logger.info("Done composing routes")
    route
  }

  override def start(): Option[Controller] = {
    val port: Int         = config.getInt("app.port")
    val interface: String = "0.0.0.0"
    logger.info(s"Starting Server at: ${interface} on port: ${port}")
    Http()
      .newServerAt(interface, port)
      .bind(routes())
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))
    logger.info(s"Server started")
    None
  }
}
