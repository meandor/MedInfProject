package com.github.meandor.doctorfate.auth.data
import java.time.LocalDateTime
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import scalikejdbc.{DB, DBSession, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.{ExecutionContext, Future, blocking}

class TokenRepository(executionContext: ExecutionContext) extends LazyLogging {
  implicit val ec: ExecutionContext = executionContext

  def toEntity(result: WrappedResultSet): TokenEntity = {
    TokenEntity(
      UUID.fromString(result.get("id")),
      result.get("email"),
      result.get("name"),
      result.get("email_is_verified"),
      result.get("created_at"),
      result.get("expires_at")
    )
  }

  def createToken(
      userID: UUID,
      createdAt: LocalDateTime,
      expiresAt: LocalDateTime
  )(implicit session: DBSession): Future[Int] = {
    Future {
      blocking {
        logger.info("Inserting Token into DB")
        sql"""
            INSERT INTO tokens (user_id, created_at, expires_at) VALUES ($userID, $createdAt, $expiresAt)
          """.update().apply()
      }
    }(executionContext)
  }

  def getTokenEntity(userID: UUID, createdAt: LocalDateTime)(
      implicit session: DBSession
  ): Future[TokenEntity] = {
    Future {
      blocking {
        logger.info("Get token entity from DB")
        sql"""
            SELECT *
            FROM users, tokens
            WHERE users.id = $userID AND tokens.user_id = $userID AND tokens.created_at = $createdAt
          """
          .map(toEntity)
          .single()
          .apply()
          .getOrElse(throw new Exception("Not able to create token"))
      }
    }(executionContext)
  }

  def create(userID: UUID): Future[TokenEntity] =
    DB futureLocalTx { implicit session =>
      logger.info("Start token creation in DB")
      val now      = LocalDateTime.now()
      val tomorrow = now.plusDays(1)
      for {
        _           <- createToken(userID, now, tomorrow)
        tokenEntity <- getTokenEntity(userID, now)
      } yield tokenEntity
    }
}
