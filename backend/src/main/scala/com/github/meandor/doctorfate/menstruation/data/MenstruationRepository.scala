package com.github.meandor.doctorfate.menstruation.data
import akka.Done
import com.typesafe.scalalogging.LazyLogging
import scalikejdbc.{DB, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future, blocking}

class MenstruationRepository(executionContext: ExecutionContext) extends LazyLogging {
  implicit val ec: ExecutionContext = executionContext

  def findByUser(userId: UUID): Future[Seq[MenstruationEntity]] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            SELECT *
            FROM menstruation
            WHERE menstruation.user_id = $userId
          """
          .map(toEntity)
          .list()
          .apply()
      }
    }
  }

  def toEntity(result: WrappedResultSet): MenstruationEntity = {
    MenstruationEntity(
      UUID.fromString(result.string("user_id")),
      result.localDate("start_date"),
      result.localDate("end_date")
    )
  }

  def find(menstruationEntity: MenstruationEntity): Future[Option[MenstruationEntity]] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            SELECT *
            FROM menstruation
            WHERE menstruation.user_id = ${menstruationEntity.userId}
            AND menstruation.start_date <= ${menstruationEntity.start}
            AND menstruation.end_date >= ${menstruationEntity.end}
          """
          .map(toEntity)
          .single()
          .apply()
      }
    }
  }

  private def insert(menstruationEntity: MenstruationEntity): Future[Int] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            INSERT INTO
            "menstruation"
            (user_id, start_date, end_date)
            VALUES
            (
              ${menstruationEntity.userId},
              ${menstruationEntity.start},
              ${menstruationEntity.end}
            )
          """
          .update()
          .apply()
      }
    }
  }

  def create(menstruationEntity: MenstruationEntity): Future[MenstruationEntity] = {
    for {
      _                         <- insert(menstruationEntity)
      createdMenstruationEntity <- find(menstruationEntity)
    } yield createdMenstruationEntity.getOrElse(
      throw new NoSuchElementException("Menstruation was not created")
    )
  }

  def delete(menstruationEntity: MenstruationEntity): Future[Int] = Future {
    blocking {
      DB localTx { implicit session =>
        sql"""
            DELETE FROM
            "menstruation"
            WHERE menstruation.user_id = ${menstruationEntity.userId}
            AND menstruation.start_date = ${menstruationEntity.start}
            AND menstruation.end_date = ${menstruationEntity.end}
          """
          .update()
          .apply()
      }
    }
  }

  def saveWithNewUserId(menstruationEntity: MenstruationEntity, newUserId: UUID): Future[Int] =
    Future {
      blocking {
        DB localTx { implicit session =>
          sql"""
            UPDATE "menstruation"
            SET user_id = $newUserId
            WHERE menstruation.user_id = ${menstruationEntity.userId}
            AND menstruation.start_date = ${menstruationEntity.start}
            AND menstruation.end_date = ${menstruationEntity.end}
          """
            .update()
            .apply()
        }
      }
    }
}
