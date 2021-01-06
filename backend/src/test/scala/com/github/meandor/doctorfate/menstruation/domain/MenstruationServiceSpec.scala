package com.github.meandor.doctorfate.menstruation.domain
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.menstruation.data.{MenstruationEntity, MenstruationRepository}
import org.mockito.ArgumentMatchers.any
import org.scalatest.concurrent.ScalaFutures

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MenstruationServiceSpec extends UnitSpec with ScalaFutures {
  Feature("create") {
    val menstruationRepository = mock[MenstruationRepository]
    val menstruationService    = new MenstruationService(menstruationRepository)

    Scenario("should return successfully created menstruation") {
      val userId = UUID.randomUUID()
      val menstruation = Menstruation(
        start = LocalDate.now(),
        end = LocalDate.now()
      )
      val createdMenstruationEntity = MenstruationEntity(
        user = userId,
        start = menstruation.start,
        end = menstruation.end
      )
      menstruationRepository.find(any[MenstruationEntity]) shouldReturn Future.successful(None)
      menstruationRepository.create(any[MenstruationEntity]) shouldReturn Future.successful(
        createdMenstruationEntity
      )

      val actual = menstruationService.create(userId, menstruation)
      val expected = Some(
        Menstruation(
          createdMenstruationEntity.start,
          createdMenstruationEntity.end
        )
      )

      actual.futureValue shouldBe expected
    }

    Scenario("should return None when time range for user already exists") {
      val userId = UUID.randomUUID()
      val menstruation = Menstruation(
        start = LocalDate.now(),
        end = LocalDate.now()
      )
      val existingMenstruationEntity = MenstruationEntity(
        user = userId,
        start = menstruation.start,
        end = menstruation.end
      )
      menstruationRepository.find(any[MenstruationEntity]) shouldReturn Future.successful(
        Some(existingMenstruationEntity)
      )

      val actual   = menstruationService.create(userId, menstruation)
      val expected = None

      actual.futureValue shouldBe expected
    }
  }
}
