package com.github.meandor.doctorfate
import java.net.URI
import java.util.concurrent.Executors

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

import scala.concurrent.ExecutionContext

final case class DatabaseModule(config: Config) extends LazyLogging {
  val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  def start(): Unit = {
    logger.info("Start db migrations")
    val dbUri    = new URI(config.getString("database.url"))
    val username = dbUri.getUserInfo.split(":")(0)
    val password = dbUri.getUserInfo.split(":")(1)
    val dbUrl =
      s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}?sslmode=require"
    val flyway = Flyway.configure.dataSource(dbUrl, username, password).load
    flyway.migrate()
    logger.info("Done db migrations")

    logger.info("Start loading DB Connection Pool")
    val initialSize             = config.getInt("database.connectionPool.initialSize")
    val maxSize                 = config.getInt("database.connectionPool.maxSize")
    val connectionTimeoutMillis = config.getLong("database.connectionPool.connectionTimeoutMillis")
    val settings = ConnectionPoolSettings(
      initialSize = initialSize,
      maxSize = maxSize,
      connectionTimeoutMillis = connectionTimeoutMillis
    )
    ConnectionPool.add('default, dbUrl, username, password, settings)
    logger.info("Done loading DB Connection Pool")
  }
}
