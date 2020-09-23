package com.github.meandor.doctorfate
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, handleExceptions, handleRejections, _}
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{cors, corsRejectionHandler}
import com.github.meandor.doctorfate.auth.presentation.TokenController
import com.github.meandor.doctorfate.presentation.BaseRoutes
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

final case class WebServer(tokenController: TokenController)(
    implicit executionContext: ExecutionContextExecutor,
    system: ActorSystem[Nothing]
) extends LazyLogging {
  val rejectionHandler: RejectionHandler =
    corsRejectionHandler.withFallback(RejectionHandler.default)

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
  }

  def routes(): Route = {
    logger.info("Start composing routes")
    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)
    val route = handleErrors {
      cors() {
        handleErrors {
          BaseRoutes.routes ~ tokenController.routes
        }
      }
    }
    logger.info("Done composing routes")
    route
  }

  def start(): Unit = {
    val maybePort: Option[String] = Option(System.getenv("PORT"))
    val defaultPort: Int          = 8080
    val port: Int                 = maybePort.fold(defaultPort)(p => p.toInt)
    val interface: String         = "0.0.0.0"
    logger.info(s"Starting Server at: ${interface} on port: ${port}")
    Http()
      .newServerAt(interface, port)
      .bind(routes())
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))
    logger.info(s"Server started")
  }
}
