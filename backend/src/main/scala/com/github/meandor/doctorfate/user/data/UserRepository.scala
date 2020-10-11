package com.github.meandor.doctorfate.user.data
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import scalikejdbc.{DB, DBSession, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.{ExecutionContext, Future, blocking}

class UserRepository(executionContext: ExecutionContext) extends LazyLogging {
  def confirm(email: String): Future[Option[UserEntity]] = ???

  implicit val ec: ExecutionContext = executionContext

  def toEntity(result: WrappedResultSet): UserEntity = {
    UserEntity(
      UUID.fromString(result.get("id")),
      result.get("email"),
      result.get("password"),
      result.get("name"),
      result.get("email_is_verified")
    )
  }

  def insert(userEntity: UserEntity): Future[Int] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            INSERT INTO
            "users"
            (id, email, password, name, email_is_verified)
            VALUES
            (
              ${userEntity.id},
              ${userEntity.email},
              ${userEntity.password},
              ${userEntity.name},
              ${userEntity.emailIsVerified}
            )
          """
          .update()
          .apply()
      }
    }
  }

  def create(userEntity: UserEntity): Future[UserEntity] = {
    logger.info("creating user")
    for {
      _                 <- insert(userEntity)
      maybeInsertedUser <- findByMail(userEntity.email)
    } yield (maybeInsertedUser.getOrElse(throw new Exception()))
  }

  def findByMail(email: String): Future[Option[UserEntity]] = Future {
    blocking {
      DB localTx { implicit session =>
        logger.info("found one user by mail")
        sql"""
            SELECT *
            FROM users
            WHERE users.email = $email
          """
          .map(toEntity)
          .single()
          .apply()
      }
    }
  }
}
