package com.github.meandor.doctorfate.menstruation.data
import scala.concurrent.Future

class MenstruationRepository {
  def find(menstruationEntity: MenstruationEntity): Future[Option[MenstruationEntity]] = ???

  def create(menstruationEntity: MenstruationEntity): Future[MenstruationEntity] = ???
}
