package com.github.meandor.doctorfate.menstruation.domain
import com.github.meandor.doctorfate.menstruation.data.{MenstruationEntity, MenstruationRepository}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class MenstruationService(menstruationRepository: MenstruationRepository)(
    implicit ec: ExecutionContext
) {
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
    } yield createdMenstruationEntity.map(entity => Menstruation(entity.start, entity.end))
  }
}
