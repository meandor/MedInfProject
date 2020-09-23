package com.github.meandor.doctorfate
import java.net.URI
import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

import scala.concurrent.ExecutionContext

final case class DatabaseModule() extends LazyLogging {
  val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  def start(): Unit = {
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
  }
}
