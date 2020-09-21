package com.github.meandor.doctorfate

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

object DoctorFate extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("Starting System");
    implicit val system: ActorSystem[Nothing]               = ActorSystem(Behaviors.empty, "doctorFate")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    logger.info("Start composing routes")
    val route: Route = BaseRoutes.routes
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
