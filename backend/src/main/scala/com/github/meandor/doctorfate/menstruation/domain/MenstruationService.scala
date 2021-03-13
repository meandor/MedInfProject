package com.github.meandor.doctorfate.menstruation.domain
import akka.Done
import com.github.meandor.doctorfate.menstruation.data.{MenstruationEntity, MenstruationRepository}
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class MenstruationService(menstruationRepository: MenstruationRepository)(
    implicit ec: ExecutionContext
) extends LazyLogging {
  private def toMenstruation(menstruationEntity: MenstruationEntity): Menstruation = {
    Menstruation(menstruationEntity.start, menstruationEntity.end)
  }

  def find(userId: UUID): Future[Seq[Menstruation]] = {
    logger.info(s"Find all menstruation for user: $userId")
    menstruationRepository.findByUser(userId).map(_.map(toMenstruation))
  }

  private def createIfNotExisting(
      toBeCreatedMenstruationEntity: MenstruationEntity,
      existingMenstruationEntity: Option[MenstruationEntity]
  ): Future[Option[MenstruationEntity]] = existingMenstruationEntity match {
    case None    => menstruationRepository.create(toBeCreatedMenstruationEntity).map(Some(_))
    case Some(_) => Future.successful(None)
  }

  def create(userId: UUID, menstruation: Menstruation): Future[Option[Menstruation]] = {
    val menstruationEntity = MenstruationEntity(userId, menstruation.start, menstruation.end)
    for {
      existingMenstruationEntity <- menstruationRepository.find(menstruationEntity)
      createdMenstruationEntity <- createIfNotExisting(
        menstruationEntity,
        existingMenstruationEntity
      )
    } yield createdMenstruationEntity.map(toMenstruation)
  }

  def delete(userId: UUID, menstruation: Menstruation): Future[Option[Done]] = {
    val menstruationEntity = MenstruationEntity(userId, menstruation.start, menstruation.end)
    menstruationRepository.delete(menstruationEntity).map {
      case 0 => None
      case _ => Some(Done)
    }
  }

  def changeOwner(userId: UUID): Future[Done] = {
    logger.info("Changing owner for user")
    for {
      menstruation <- menstruationRepository.findByUser(userId)
      newUserId = UUID.randomUUID()
      _ <- Future.sequence(
        menstruation.map(m => menstruationRepository.saveWithNewUserId(m, newUserId))
      )
      _ = logger.info("Successfully changed owner of menstruation")
    } yield Done
  }
}
