package com.github.meandor.doctorfate.menstruation.domain
import java.util.UUID
import scala.concurrent.Future

class MenstruationService {
  def create(userId: UUID, menstruation: Menstruation): Future[Option[Menstruation]] = ???
}
