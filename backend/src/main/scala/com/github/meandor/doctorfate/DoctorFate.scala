package com.github.meandor.doctorfate

import java.net.URI
import java.util.concurrent.Executors

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.github.meandor.doctorfate.auth.AuthModule
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object DoctorFate extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("Starting System")
    implicit val system: ActorSystem[Nothing]               = ActorSystem(Behaviors.empty, "doctorFate")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    logger.info("Start db migrations")
    val dbUri    = new URI(System.getenv("DATABASE_URL"))
    val username = dbUri.getUserInfo.split(":")(0)
    val password = dbUri.getUserInfo.split(":")(1)
    val dbUrl =
      s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}?sslmode=require"
    val flyway = Flyway.configure.dataSource(dbUrl, username, password).load
    flyway.migrate()
    logger.info("Done db migrations")

    logger.info("Start connecting to DB")
    val settings = ConnectionPoolSettings(
      initialSize = 5,
      maxSize = 20,
      connectionTimeoutMillis = 3000L
    )
    ConnectionPool.add('default, dbUrl, username, password, settings)
    logger.info("Done connecting to DB")

    val databaseEC      = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
    val jwtIDSecret     = System.getenv("JWT_ID_SECRET")
    val jwtAccessSecret = System.getenv("JWT_ACCESS_SECRET")
    val passwordSalt    = System.getenv("PASSWORD_SALT")
    val tokenController = AuthModule.start(jwtIDSecret, jwtAccessSecret, passwordSalt, databaseEC)

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
