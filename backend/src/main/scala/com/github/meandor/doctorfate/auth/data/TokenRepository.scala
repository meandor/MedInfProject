package com.github.meandor.doctorfate.auth.data
import java.time.LocalDateTime
import java.util.UUID

import scalikejdbc.{DB, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.{ExecutionContext, Future, blocking}

class TokenRepository(executionContext: ExecutionContext) {
  def toEntity(result: WrappedResultSet): TokenEntity = {
    TokenEntity(
      UUID.fromString(result.get("id")),
      result.get("email"),
      result.get("name"),
      result.get("emailIsVerified"),
      result.get("created_at"),
      result.get("expires_at")
    )
  }

  def create(userID: UUID): Future[TokenEntity] =
    Future {
      blocking {
        DB autoCommit { implicit session =>
          val now      = LocalDateTime.now()
          val tomorrow = now.plusDays(1)
          sql"""
            INSERT INTO tokens (user_id, created_at, expires_at) VALUES ($userID, $now, $tomorrow)
          """.update().apply()

          sql"""
            SELECT *
            FROM users, tokens
            WHERE users.id = $userID AND tokens.user_id = $userID AND tokens.created_at = $now
          """.map(toEntity).single().apply().get
        }
      }
    }(executionContext)
}
