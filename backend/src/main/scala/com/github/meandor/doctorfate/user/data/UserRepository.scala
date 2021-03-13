package com.github.meandor.doctorfate.user.data
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import scalikejdbc.{DB, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.{ExecutionContext, Future, blocking}

class UserRepository(executionContext: ExecutionContext) extends LazyLogging {
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

  def confirm(email: String): Future[Option[UserEntity]] = Future {
    blocking {
      DB localTx { implicit session =>
        logger.info("confirming a user")
        val updated = sql"""
            UPDATE users
            SET email_is_verified=true
            WHERE users.email = $email AND users.email_is_verified = false
          """.update().apply()
        if (updated == 1) {
          sql"""
            SELECT * FROM users WHERE users.email = $email
          """
            .map(toEntity)
            .single()
            .apply()
        } else {
          None
        }
      }
    }
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
    } yield maybeInsertedUser.getOrElse(throw new NoSuchElementException("User was not created"))
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

  def find(userId: UUID): Future[Option[UserEntity]] = Future {
    blocking {
      DB localTx { implicit session =>
        logger.info("found one user by mail")
        sql"""
            SELECT *
            FROM users
            WHERE users.id = $userId
          """
          .map(toEntity)
          .single()
          .apply()
      }
    }
  }

  def delete(userId: UUID): Future[Int] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            DELETE FROM
            "users"
            WHERE user_id = $userId
          """
          .update()
          .apply()
      }
    }
  }

}
