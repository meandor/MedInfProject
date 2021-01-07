package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.meandor.doctorfate.UnitSpec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.time.LocalDate
import java.util.UUID

class PredictionControllerSpec extends UnitSpec with ScalatestRouteTest {
  val authenticator: Credentials => Option[UUID] = {
    case _ @Credentials.Provided("accessToken") => Some(UUID.randomUUID())
    case _                                      => None
  }
  val controller: PredictionController = new PredictionController(authenticator)

  Feature("GET /prediction") {
    Scenario("should return 200 and prediction for valid user") {
      Get("/prediction") ~>
        addCredentials(OAuth2BearerToken("accessToken")) ~>
        Route.seal(controller.routes) ~>
        check {
          val actual    = responseAs[PredictionDTO]
          val ovulation = OvulationPredictionDTO(startDate = LocalDate.now(), isActive = false)
          val menstruation = MenstruationPredictionDTO(
            startDate = LocalDate.now(),
            isActive = true,
            duration = 5
          )
          val expected = PredictionDTO(ovulation, menstruation)

          status shouldBe StatusCodes.OK
          actual shouldBe expected
        }
    }

    Scenario("should return AuthorizationFailedRejection for unauthorized user") {
      Get("/prediction") ~>
        addCredentials(OAuth2BearerToken("foo")) ~>
        controller.routes ~>
        check {
          rejection shouldBe a[AuthenticationFailedRejection]
        }
    }

    Scenario("should return AuthorizationFailedRejection for missing user") {
      Get("/prediction") ~> controller.routes ~> check {
        rejection shouldBe a[AuthenticationFailedRejection]
      }
    }
  }
}
