package com.github.meandor.doctorfate.menstruation.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.auth.domain.AccessToken
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.{Date, UUID}

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
        addCredentials(OAuth2BearerToken(accessJWT)) ~>
        Route.seal(controller.routes) ~>
        check {
          val actual    = responseAs[PredictionDTO]
          val ovulation = OvulationDTO(startDate = LocalDate.now(), isActive = false)
          val period    = MenstruationDTO(startDate = LocalDate.now(), isActive = true, duration = 5)
          val expected  = PredictionDTO(ovulation, period)

          status shouldBe StatusCodes.OK
          actual shouldBe expected
        }
    }

    Scenario("should return AuthorizationFailedRejection for unauthorized user") {
      Get("/menstruation/prediction") ~>
        addCredentials(OAuth2BearerToken("foo")) ~>
        controller.routes ~>
        check {
          rejection shouldBe a[AuthenticationFailedRejection]
        }
    }

    Scenario("should return AuthorizationFailedRejection for missing user") {
      Get("/menstruation/prediction") ~> controller.routes ~> check {
        rejection shouldBe a[AuthenticationFailedRejection]
      }
    }
  }
}
