package com.github.meandor.doctorfate.menstruation.presentation
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.{Date, UUID}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.server.{MissingCookieRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.auth.domain.AccessToken
import com.github.meandor.doctorfate.core.presentation.{Controller, ErrorDTO}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class PredictionControllerSpec extends UnitSpec with ScalatestRouteTest {
  val secret: String                   = "secret"
  val controller: PredictionController = new PredictionController(secret)

  Feature("GET /menstruation/prediction") {
    val accessToken = AccessToken(UUID.randomUUID())
    val algorithm   = Algorithm.HMAC512(secret)
    val jwtBuilder = JWT
      .create()
      .withIssuer("doctor-fate")
    val now            = LocalDateTime.now()
    val in10Hours      = now.plusHours(10L)
    val tomorrow       = now.plusHours(24L)
    val zone           = ZoneId.of("Europe/Berlin")
    val berlinTimeZone = zone.getRules.getOffset(in10Hours)
    val accessJWT = jwtBuilder
      .withSubject(accessToken.userID.toString)
      .withExpiresAt(Date.from(tomorrow.toInstant(berlinTimeZone)))
      .sign(algorithm)

    Scenario("should return 200 and prediction for valid user") {
      Get("/menstruation/prediction") ~>
        Cookie(Controller.accessTokenCookieName -> accessJWT) ~>
        Route.seal(controller.routes) ~>
        check {
          val actual    = responseAs[PredictionDTO]
          val ovulation = OvulationDTO(startDate = LocalDate.now(), isActive = false)
          val period    = PeriodDTO(startDate = LocalDate.now(), isActive = false, duration = 5)
          val expected  = PredictionDTO(ovulation, period)

          status shouldBe StatusCodes.OK
          actual shouldBe expected
        }
    }

    Scenario("should return 401 for unauthorized user") {
      Get("/menstruation/prediction") ~>
        Cookie(Controller.accessTokenCookieName -> "foo") ~>
        Route.seal(controller.routes) ~>
        check {
          val actual   = responseAs[ErrorDTO]
          val expected = ErrorDTO("Invalid user")

          status shouldBe StatusCodes.Unauthorized
          actual shouldBe expected
        }
    }

    Scenario("should return MissingCookieRejection(ACCESS_TOKEN) for missing user") {
      Get("/menstruation/prediction") ~> controller.routes ~> check {
        rejection shouldEqual MissingCookieRejection(Controller.accessTokenCookieName)
      }
    }
  }
}
