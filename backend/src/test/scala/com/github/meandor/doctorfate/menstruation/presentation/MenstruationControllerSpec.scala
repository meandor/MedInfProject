package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.menstruation.domain.{Menstruation, MenstruationService}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.mockito.ArgumentMatchers.any

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

class MenstruationControllerSpec extends UnitSpec with ScalatestRouteTest {
  val authenticator: Credentials => Option[UUID] = {
    case _ @Credentials.Provided("accessToken") => Some(UUID.randomUUID())
    case _                                      => None
  }
  val serviceMock: MenstruationService   = mock[MenstruationService]
  val controller: MenstruationController = new MenstruationController(authenticator, serviceMock)

  Feature("POST /menstruation") {
    val firstOfJanuary = LocalDate.of(2021, 1, 1)
    val fifthOfJanuary = LocalDate.of(2021, 1, 5)

    Scenario("should return 200 and create menstruation for valid user") {
      val createdMenstruation = Menstruation(start = firstOfJanuary, end = fifthOfJanuary)
      serviceMock.create(any[UUID], any[Menstruation]) shouldReturn Future.successful(
        Some(createdMenstruation)
      )

      Post("/menstruation", MenstruationDTO(createdMenstruation.start, createdMenstruation.end)) ~>
        addCredentials(OAuth2BearerToken("accessToken")) ~>
        Route.seal(controller.routes) ~>
        check {
          val actual = responseAs[MenstruationDTO]
          val expected = MenstruationDTO(
            start = createdMenstruation.start,
            end = createdMenstruation.end
          )

          status shouldBe StatusCodes.OK
          actual shouldBe expected
        }
    }

    Scenario("should return 400 for valid user when created menstruation is None") {
      val createdMenstruation = Menstruation(start = firstOfJanuary, end = fifthOfJanuary)
      serviceMock.create(any[UUID], any[Menstruation]) shouldReturn Future.successful(None)

      Post("/menstruation", MenstruationDTO(createdMenstruation.start, createdMenstruation.end)) ~>
        addCredentials(OAuth2BearerToken("accessToken")) ~>
        Route.seal(controller.routes) ~>
        check {
          status shouldBe StatusCodes.BadRequest
        }
    }

    Scenario("should return 400 for invalid dates and valid user") {
      Post("/menstruation", MenstruationDTO(fifthOfJanuary, firstOfJanuary)) ~>
        addCredentials(OAuth2BearerToken("accessToken")) ~>
        Route.seal(controller.routes) ~>
        check {
          status shouldBe StatusCodes.BadRequest
        }
    }
  }

  Feature("GET /menstruation") {
    Scenario("should return 200 and available menstruation for user") {
      serviceMock.find(any[UUID]) shouldReturn Future.successful(Seq())

      Get("/menstruation") ~>
        addCredentials(OAuth2BearerToken("accessToken")) ~>
        Route.seal(controller.routes) ~>
        check {
          val actual   = responseAs[Seq[MenstruationDTO]]
          val expected = Seq()
          status shouldBe StatusCodes.OK
          actual shouldBe expected
        }
    }
  }
}
