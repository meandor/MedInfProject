package com.github.meandor.doctorfate.menstruation.data
import com.github.meandor.doctorfate.DatabaseIntegrationSpec
import org.scalatest.time.{Millis, Seconds, Span}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MenstruationRepositorySpec extends DatabaseIntegrationSpec {
  val userRepository = new MenstruationRepository(global)
  implicit val defaultPatience: PatienceConfig = PatienceConfig(
    timeout = Span(1, Seconds),
    interval = Span(500, Millis)
  )

  Feature("create") {
    Scenario("should create and return created menstruation") { _ =>
      val toBeInsertedEntity = MenstruationEntity(
        UUID.randomUUID(),
        LocalDate.now(),
        LocalDate.now()
      )

      val actual   = userRepository.create(toBeInsertedEntity)
      val expected = Future.successful(toBeInsertedEntity)

      actual.futureValue shouldBe expected.futureValue
    }
  }
}
