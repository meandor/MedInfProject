package com.github.meandor.doctorfate.core
import akka.actor.typed.ActorSystem
import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  AuthenticationFailedRejection,
  ExceptionHandler,
  RejectionHandler,
  Route
}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{cors, corsRejectionHandler}
import com.github.meandor.doctorfate.core.presentation.{BaseRoutes, Controller}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

final case class WebServer(config: Config, controllers: Seq[Controller])(
    implicit executionContext: ExecutionContextExecutor,
    system: ActorSystem[Nothing]
) extends Module {
  val rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case _: AuthenticationFailedRejection =>
          Controller.unauthorized
      }
      .handle {
        case _: AuthorizationFailedRejection =>
          Controller.unauthorized
      }
      .result()

  val rejectionHandlerWithCors: RejectionHandler =
    corsRejectionHandler.withFallback(rejectionHandler).withFallback(RejectionHandler.default)

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
    case e: Exception =>
      logger.error("Error processing request", e)
      Controller.failedResponse
  }

  def routes(): Route = {
    logger.info("Start composing routes")
    val handleErrors =
      handleRejections(rejectionHandlerWithCors) & handleExceptions(exceptionHandler)
    val route = handleErrors {
      cors() {
        handleErrors {
          controllers.foldLeft(BaseRoutes.routes) { (acc, controller) => acc ~ controller.routes }
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
